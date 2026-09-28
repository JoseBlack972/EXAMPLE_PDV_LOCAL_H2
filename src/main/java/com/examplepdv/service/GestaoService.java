package com.examplepdv.service;

import com.examplepdv.dto.RelatorioGestaoDTO;
import com.examplepdv.dto.TempoRealCaixaDTO;
import com.examplepdv.model.*;
import com.examplepdv.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GestaoService {

    private final VendaRepository vendaRepository;
    private final CaixaRepository caixaRepository;
    private final MovimentoCaixaRepository movimentoRepository;
    private final ProdutoRepository produtoRepository;
    private final EmpresaUtilizadoraService empresaService;
    private final CaixaService caixaService;

    public GestaoService(VendaRepository vendaRepository,
                         CaixaRepository caixaRepository,
                         MovimentoCaixaRepository movimentoRepository,
                         ProdutoRepository produtoRepository,
                         EmpresaUtilizadoraService empresaService,
                         CaixaService caixaService) {
        this.vendaRepository = vendaRepository;
        this.caixaRepository = caixaRepository;
        this.movimentoRepository = movimentoRepository;
        this.produtoRepository = produtoRepository;
        this.empresaService = empresaService;
        this.caixaService = caixaService;
    }

    @Transactional(readOnly = true)
    public TempoRealCaixaDTO obterTempoRealCaixa() {
        TempoRealCaixaDTO dto = new TempoRealCaixaDTO();
        Optional<Caixa> caixaOpt = caixaService.obterCaixaAberto();

        if (caixaOpt.isEmpty()) {
            dto.setTemCaixaAberto(false);
            return dto;
        }

        Caixa caixa = caixaOpt.get();
        dto.setTemCaixaAberto(true);
        dto.setCaixaId(caixa.getId());
        dto.setOperadorNome(caixa.getUsuario().getNome());
        dto.setOperadorUsername(caixa.getUsuario().getUsername());
        dto.setDataAbertura(caixa.getDataAbertura());
        dto.setSaldoInicial(caixa.getSaldoInicial() != null ? caixa.getSaldoInicial() : BigDecimal.ZERO);

        List<Venda> vendas = vendaRepository.findByCaixaOrderByIdDesc(caixa);
        dto.setQuantidadeVendas(vendas.size());

        BigDecimal totalVendido = BigDecimal.ZERO;
        BigDecimal din = BigDecimal.ZERO;
        BigDecimal pix = BigDecimal.ZERO;
        BigDecimal deb = BigDecimal.ZERO;
        BigDecimal cred = BigDecimal.ZERO;

        for (Venda v : vendas) {
            BigDecimal val = v.getValorTotal() != null ? v.getValorTotal() : BigDecimal.ZERO;
            totalVendido = totalVendido.add(val);

            if (v.getFormaPagamento() == FormaPagamento.DINHEIRO) din = din.add(val);
            else if (v.getFormaPagamento() == FormaPagamento.PIX) pix = pix.add(val);
            else if (v.getFormaPagamento() == FormaPagamento.CARTAO_DEBITO) deb = deb.add(val);
            else if (v.getFormaPagamento() == FormaPagamento.CARTAO_CREDITO) cred = cred.add(val);
        }

        dto.setTotalVendido(totalVendido);
        dto.setTotalDinheiro(din);
        dto.setTotalPix(pix);
        dto.setTotalDebito(deb);
        dto.setTotalCredito(cred);

        if (vendas.size() > 0) {
            dto.setTicketMedio(totalVendido.divide(BigDecimal.valueOf(vendas.size()), 2, RoundingMode.HALF_UP));
        }

        // Movimentações (sangrias e suprimentos)
        List<MovimentoCaixa> movimentos = movimentoRepository.findByCaixaOrderByDataHoraDesc(caixa);
        BigDecimal sangrias = BigDecimal.ZERO;
        BigDecimal suprimentos = BigDecimal.ZERO;

        for (MovimentoCaixa m : movimentos) {
            if (m.getTipo() == TipoMovimentoCaixa.SANGRIA) {
                sangrias = sangrias.add(m.getValor());
            } else if (m.getTipo() == TipoMovimentoCaixa.SUPRIMENTO) {
                suprimentos = suprimentos.add(m.getValor());
            }
        }

        dto.setTotalSangrias(sangrias);
        dto.setTotalSuprimentos(suprimentos);

        // Saldo Gaveta Estimado = Saldo Inicial + Vendas em Dinheiro + Suprimentos - Sangrias
        BigDecimal saldoGaveta = dto.getSaldoInicial().add(din).add(suprimentos).subtract(sangrias);
        dto.setSaldoGavetaEstimado(saldoGaveta);

        // Últimas 10 vendas
        List<TempoRealCaixaDTO.UltimaVendaDTO> ultimas = new ArrayList<>();
        int limite = Math.min(vendas.size(), 10);
        for (int i = 0; i < limite; i++) {
            Venda v = vendas.get(i);
            ultimas.add(new TempoRealCaixaDTO.UltimaVendaDTO(
                    v.getId(),
                    v.getDataHora(),
                    v.getValorTotal(),
                    v.getFormaPagamento().getDescricao(),
                    v.getItens() != null ? v.getItens().size() : 0,
                    v.getUsuario().getNome()
            ));
        }
        dto.setUltimasVendas(ultimas);

        return dto;
    }

    @Transactional(readOnly = true)
    public RelatorioGestaoDTO gerarRelatorio(String periodo, String tipoRelatorio, LocalDate dataInicioCustom, LocalDate dataFimCustom, String gestorNome) {
        RelatorioGestaoDTO relatorio = new RelatorioGestaoDTO();
        relatorio.setDataHoraGeracao(LocalDateTime.now());
        relatorio.setGestorNome(gestorNome);
        relatorio.setEmpresa(empresaService.obterEmpresa());

        LocalDate hoje = LocalDate.now();
        LocalDate inicio;
        LocalDate fim;

        if ("SEMANA".equalsIgnoreCase(periodo)) {
            inicio = hoje.minusDays(6);
            fim = hoje;
            relatorio.setPeriodo("Últimos 7 Dias (Semana)");
        } else if ("MES".equalsIgnoreCase(periodo)) {
            inicio = hoje.withDayOfMonth(1);
            fim = hoje;
            relatorio.setPeriodo("Mês Atual (" + hoje.format(DateTimeFormatter.ofPattern("MM/yyyy")) + ")");
        } else if ("PERSONALIZADO".equalsIgnoreCase(periodo) && dataInicioCustom != null && dataFimCustom != null) {
            inicio = dataInicioCustom;
            fim = dataFimCustom;
            relatorio.setPeriodo("Personalizado (" + inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a " + fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")");
        } else {
            inicio = hoje;
            fim = hoje;
            relatorio.setPeriodo("Hoje (" + hoje.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")");
        }

        relatorio.setDataInicio(inicio);
        relatorio.setDataFim(fim);
        relatorio.setTipoRelatorio(tipoRelatorio != null ? tipoRelatorio.toUpperCase() : "CONSOLIDADO");

        LocalDateTime dataHoraInicio = inicio.atStartOfDay();
        LocalDateTime dataHoraFim = fim.atTime(23, 59, 59, 999999999);

        // 1. Busca Vendas do Período
        List<Venda> vendas = vendaRepository.findByDataHoraBetweenOrderByIdDesc(dataHoraInicio, dataHoraFim);
        relatorio.setVendasDetalhadas(vendas);
        relatorio.setQuantidadeVendas(vendas.size());

        BigDecimal totalValor = BigDecimal.ZERO;
        BigDecimal totalDin = BigDecimal.ZERO;
        BigDecimal totalPix = BigDecimal.ZERO;
        BigDecimal totalDeb = BigDecimal.ZERO;
        BigDecimal totalCred = BigDecimal.ZERO;
        int totalItens = 0;

        Map<Long, RelatorioGestaoDTO.TopProdutoDTO> mapTopProdutos = new HashMap<>();

        for (Venda v : vendas) {
            BigDecimal vTotal = v.getValorTotal() != null ? v.getValorTotal() : BigDecimal.ZERO;
            totalValor = totalValor.add(vTotal);

            if (v.getFormaPagamento() == FormaPagamento.DINHEIRO) totalDin = totalDin.add(vTotal);
            else if (v.getFormaPagamento() == FormaPagamento.PIX) totalPix = totalPix.add(vTotal);
            else if (v.getFormaPagamento() == FormaPagamento.CARTAO_DEBITO) totalDeb = totalDeb.add(vTotal);
            else if (v.getFormaPagamento() == FormaPagamento.CARTAO_CREDITO) totalCred = totalCred.add(vTotal);

            if (v.getItens() != null) {
                totalItens += v.getItens().size();
                for (ItemVenda iv : v.getItens()) {
                    Produto p = iv.getProduto();
                    if (p != null) {
                        RelatorioGestaoDTO.TopProdutoDTO top = mapTopProdutos.computeIfAbsent(p.getId(), k -> 
                                new RelatorioGestaoDTO.TopProdutoDTO(
                                        p.getNome(),
                                        p.getCodigoBarras(),
                                        p.getCategoria() != null ? p.getCategoria().getNome() : "Geral",
                                        0,
                                        BigDecimal.ZERO
                                )
                        );
                        int novaQtd = top.getQuantidadeVendida() + (iv.getQuantidade() != null ? iv.getQuantidade() : 1);
                        BigDecimal novoFat = top.getTotalFaturado().add(iv.getSubtotal() != null ? iv.getSubtotal() : BigDecimal.ZERO);
                        mapTopProdutos.put(p.getId(), new RelatorioGestaoDTO.TopProdutoDTO(top.getNome(), top.getCodigoBarras(), top.getCategoria(), novaQtd, novoFat));
                    }
                }
            }
        }

        relatorio.setTotalVendasValor(totalValor);
        relatorio.setTotalItensVendidos(totalItens);
        relatorio.setTotalDinheiro(totalDin);
        relatorio.setTotalPix(totalPix);
        relatorio.setTotalDebito(totalDeb);
        relatorio.setTotalCredito(totalCred);

        if (vendas.size() > 0) {
            relatorio.setTicketMedio(totalValor.divide(BigDecimal.valueOf(vendas.size()), 2, RoundingMode.HALF_UP));
        }

        if (totalValor.compareTo(BigDecimal.ZERO) > 0) {
            relatorio.setPercDinheiro(totalDin.multiply(BigDecimal.valueOf(100)).divide(totalValor, 1, RoundingMode.HALF_UP).doubleValue());
            relatorio.setPercPix(totalPix.multiply(BigDecimal.valueOf(100)).divide(totalValor, 1, RoundingMode.HALF_UP).doubleValue());
            relatorio.setPercDebito(totalDeb.multiply(BigDecimal.valueOf(100)).divide(totalValor, 1, RoundingMode.HALF_UP).doubleValue());
            relatorio.setPercCredito(totalCred.multiply(BigDecimal.valueOf(100)).divide(totalValor, 1, RoundingMode.HALF_UP).doubleValue());
        }

        // Top 10 Produtos ordenados por quantidade
        List<RelatorioGestaoDTO.TopProdutoDTO> topList = new ArrayList<>(mapTopProdutos.values());
        topList.sort((a, b) -> Integer.compare(b.getQuantidadeVendida(), a.getQuantidadeVendida()));
        if (topList.size() > 10) {
            topList = topList.subList(0, 10);
        }
        relatorio.setTopProdutos(topList);

        // 2. Vendas por Faixa de Horário
        relatorio.setVendasPorHorario(agruparVendasPorHorario(vendas, totalValor));

        // 3. Vendas por Caixa
        relatorio.setVendasPorCaixa(agruparVendasPorCaixa(vendas));

        // 4. Vendas por Dia
        relatorio.setVendasPorDia(agruparVendasPorDia(vendas, inicio, fim));

        // 5. Caixas e Movimentações do Período
        List<Caixa> caixas = caixaRepository.findByDataAberturaBetweenOrderByDataAberturaDesc(dataHoraInicio, dataHoraFim);
        relatorio.setCaixasDetalhados(caixas);
        relatorio.setQuantidadeCaixas(caixas.size());

        List<MovimentoCaixa> movimentos = movimentoRepository.findByDataHoraBetweenOrderByDataHoraDesc(dataHoraInicio, dataHoraFim);
        relatorio.setMovimentosDetalhados(movimentos);

        BigDecimal sup = BigDecimal.ZERO;
        BigDecimal sang = BigDecimal.ZERO;
        for (MovimentoCaixa m : movimentos) {
            if (m.getTipo() == TipoMovimentoCaixa.SUPRIMENTO) sup = sup.add(m.getValor());
            else if (m.getTipo() == TipoMovimentoCaixa.SANGRIA) sang = sang.add(m.getValor());
        }
        relatorio.setTotalSuprimentos(sup);
        relatorio.setTotalSangrias(sang);

        BigDecimal divergencias = BigDecimal.ZERO;
        for (Caixa c : caixas) {
            if (c.getDiferenca() != null) {
                divergencias = divergencias.add(c.getDiferenca());
            }
        }
        relatorio.setTotalDivergencias(divergencias);

        // 6. Dados de Estoque Atual
        List<Produto> todosProdutos = produtoRepository.findAll();
        relatorio.setTodosProdutos(todosProdutos);

        int totalAtivos = 0;
        int unidadesEstoque = 0;
        BigDecimal valEstoque = BigDecimal.ZERO;
        List<Produto> estoqueBaixo = new ArrayList<>();

        for (Produto p : todosProdutos) {
            if (Boolean.TRUE.equals(p.isAtivo())) {
                totalAtivos++;
                int est = p.getEstoque() != null ? p.getEstoque() : 0;
                unidadesEstoque += est;
                if (p.getPreco() != null) {
                    valEstoque = valEstoque.add(p.getPreco().multiply(BigDecimal.valueOf(est)));
                }
                if (est <= 5) {
                    estoqueBaixo.add(p);
                }
            }
        }
        relatorio.setTotalProdutosAtivos(totalAtivos);
        relatorio.setTotalUnidadesEstoque(unidadesEstoque);
        relatorio.setValorTotalEstoque(valEstoque);
        relatorio.setProdutosEstoqueBaixo(estoqueBaixo);

        return relatorio;
    }

    private List<RelatorioGestaoDTO.VendaHorarioDTO> agruparVendasPorHorario(List<Venda> vendas, BigDecimal totalGeral) {
        String[] faixas = {
                "06:00 - 08:00", "08:00 - 10:00", "10:00 - 12:00", "12:00 - 14:00",
                "14:00 - 16:00", "16:00 - 18:00", "18:00 - 20:00", "20:00 - 22:00",
                "22:00 - 00:00", "00:00 - 06:00"
        };

        Map<String, Integer> qtdMap = new LinkedHashMap<>();
        Map<String, BigDecimal> valMap = new LinkedHashMap<>();
        for (String f : faixas) {
            qtdMap.put(f, 0);
            valMap.put(f, BigDecimal.ZERO);
        }

        for (Venda v : vendas) {
            int hora = v.getDataHora().getHour();
            String f;
            if (hora >= 6 && hora < 8) f = "06:00 - 08:00";
            else if (hora >= 8 && hora < 10) f = "08:00 - 10:00";
            else if (hora >= 10 && hora < 12) f = "10:00 - 12:00";
            else if (hora >= 12 && hora < 14) f = "12:00 - 14:00";
            else if (hora >= 14 && hora < 16) f = "14:00 - 16:00";
            else if (hora >= 16 && hora < 18) f = "16:00 - 18:00";
            else if (hora >= 18 && hora < 20) f = "18:00 - 20:00";
            else if (hora >= 20 && hora < 22) f = "20:00 - 22:00";
            else if (hora >= 22) f = "22:00 - 00:00";
            else f = "00:00 - 06:00";

            qtdMap.put(f, qtdMap.get(f) + 1);
            valMap.put(f, valMap.get(f).add(v.getValorTotal() != null ? v.getValorTotal() : BigDecimal.ZERO));
        }

        List<RelatorioGestaoDTO.VendaHorarioDTO> list = new ArrayList<>();
        for (String f : faixas) {
            int q = qtdMap.get(f);
            BigDecimal v = valMap.get(f);
            double perc = 0.0;
            if (totalGeral.compareTo(BigDecimal.ZERO) > 0) {
                perc = v.multiply(BigDecimal.valueOf(100)).divide(totalGeral, 1, RoundingMode.HALF_UP).doubleValue();
            }
            list.add(new RelatorioGestaoDTO.VendaHorarioDTO(f, q, v, perc));
        }
        return list;
    }

    private List<RelatorioGestaoDTO.VendaCaixaDTO> agruparVendasPorCaixa(List<Venda> vendas) {
        Map<Long, List<Venda>> porCaixa = vendas.stream()
                .filter(v -> v.getCaixa() != null)
                .collect(Collectors.groupingBy(v -> v.getCaixa().getId()));

        List<RelatorioGestaoDTO.VendaCaixaDTO> list = new ArrayList<>();
        for (Map.Entry<Long, List<Venda>> entry : porCaixa.entrySet()) {
            List<Venda> vs = entry.getValue();
            Caixa c = vs.get(0).getCaixa();
            BigDecimal soma = vs.stream()
                    .map(v -> v.getValorTotal() != null ? v.getValorTotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            list.add(new RelatorioGestaoDTO.VendaCaixaDTO(
                    c.getId(),
                    c.getUsuario() != null ? c.getUsuario().getNome() : "Desconhecido",
                    c.getDataAbertura(),
                    c.getDataFechamento(),
                    c.getStatus() != null ? c.getStatus().getDescricao() : "-",
                    vs.size(),
                    soma
            ));
        }
        list.sort((a, b) -> Long.compare(b.getCaixaId(), a.getCaixaId()));
        return list;
    }

    private List<RelatorioGestaoDTO.VendaDiaDTO> agruparVendasPorDia(List<Venda> vendas, LocalDate inicio, LocalDate fim) {
        Map<LocalDate, List<Venda>> porDia = vendas.stream()
                .collect(Collectors.groupingBy(v -> v.getDataHora().toLocalDate()));

        List<RelatorioGestaoDTO.VendaDiaDTO> list = new ArrayList<>();
        LocalDate curr = inicio;
        while (!curr.isAfter(fim)) {
            List<Venda> vs = porDia.getOrDefault(curr, Collections.emptyList());
            BigDecimal din = BigDecimal.ZERO;
            BigDecimal outros = BigDecimal.ZERO;
            BigDecimal total = BigDecimal.ZERO;

            for (Venda v : vs) {
                BigDecimal val = v.getValorTotal() != null ? v.getValorTotal() : BigDecimal.ZERO;
                total = total.add(val);
                if (v.getFormaPagamento() == FormaPagamento.DINHEIRO) {
                    din = din.add(val);
                } else {
                    outros = outros.add(val);
                }
            }

            list.add(new RelatorioGestaoDTO.VendaDiaDTO(curr, vs.size(), din, outros, total));
            curr = curr.plusDays(1);
        }
        return list;
    }
}
