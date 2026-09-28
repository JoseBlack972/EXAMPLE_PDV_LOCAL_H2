package com.examplepdv.dto;

import com.examplepdv.model.Caixa;
import com.examplepdv.model.EmpresaUtilizadora;
import com.examplepdv.model.MovimentoCaixa;
import com.examplepdv.model.Produto;
import com.examplepdv.model.Venda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RelatorioGestaoDTO {
    private String periodo = "HOJE";
    private String tipoRelatorio = "CONSOLIDADO";
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private LocalDateTime dataHoraGeracao;
    private String gestorNome;
    private EmpresaUtilizadora empresa;

    // Métricas de Vendas
    private BigDecimal totalVendasValor = BigDecimal.ZERO;
    private int quantidadeVendas = 0;
    private BigDecimal ticketMedio = BigDecimal.ZERO;
    private int totalItensVendidos = 0;
    private BigDecimal totalDinheiro = BigDecimal.ZERO;
    private BigDecimal totalPix = BigDecimal.ZERO;
    private BigDecimal totalDebito = BigDecimal.ZERO;
    private BigDecimal totalCredito = BigDecimal.ZERO;
    private double percDinheiro = 0.0;
    private double percPix = 0.0;
    private double percDebito = 0.0;
    private double percCredito = 0.0;

    // Métricas de Caixa
    private BigDecimal totalSuprimentos = BigDecimal.ZERO;
    private BigDecimal totalSangrias = BigDecimal.ZERO;
    private BigDecimal totalDivergencias = BigDecimal.ZERO;
    private int quantidadeCaixas = 0;

    // Métricas de Estoque
    private int totalProdutosAtivos = 0;
    private int totalUnidadesEstoque = 0;
    private BigDecimal valorTotalEstoque = BigDecimal.ZERO;

    // Listas agrupadas e detalhadas
    private List<VendaHorarioDTO> vendasPorHorario = new ArrayList<>();
    private List<VendaCaixaDTO> vendasPorCaixa = new ArrayList<>();
    private List<VendaDiaDTO> vendasPorDia = new ArrayList<>();
    private List<TopProdutoDTO> topProdutos = new ArrayList<>();
    private List<Venda> vendasDetalhadas = new ArrayList<>();
    private List<Caixa> caixasDetalhados = new ArrayList<>();
    private List<MovimentoCaixa> movimentosDetalhados = new ArrayList<>();
    private List<Produto> produtosEstoqueBaixo = new ArrayList<>();
    private List<Produto> todosProdutos = new ArrayList<>();

    // Sub-classes para agrupamento
    public static class VendaHorarioDTO {
        private String faixa;
        private int quantidadeVendas;
        private BigDecimal totalValor;
        private double percentual;

        public VendaHorarioDTO(String faixa, int quantidadeVendas, BigDecimal totalValor, double percentual) {
            this.faixa = faixa;
            this.quantidadeVendas = quantidadeVendas;
            this.totalValor = totalValor;
            this.percentual = percentual;
        }

        public String getFaixa() { return faixa; }
        public int getQuantidadeVendas() { return quantidadeVendas; }
        public BigDecimal getTotalValor() { return totalValor; }
        public double getPercentual() { return percentual; }
    }

    public static class VendaCaixaDTO {
        private Long caixaId;
        private String operadorNome;
        private LocalDateTime dataAbertura;
        private LocalDateTime dataFechamento;
        private String status;
        private int quantidadeVendas;
        private BigDecimal totalValor;

        public VendaCaixaDTO(Long caixaId, String operadorNome, LocalDateTime dataAbertura, LocalDateTime dataFechamento, String status, int quantidadeVendas, BigDecimal totalValor) {
            this.caixaId = caixaId;
            this.operadorNome = operadorNome;
            this.dataAbertura = dataAbertura;
            this.dataFechamento = dataFechamento;
            this.status = status;
            this.quantidadeVendas = quantidadeVendas;
            this.totalValor = totalValor;
        }

        public Long getCaixaId() { return caixaId; }
        public String getOperadorNome() { return operadorNome; }
        public LocalDateTime getDataAbertura() { return dataAbertura; }
        public LocalDateTime getDataFechamento() { return dataFechamento; }
        public String getStatus() { return status; }
        public int getQuantidadeVendas() { return quantidadeVendas; }
        public BigDecimal getTotalValor() { return totalValor; }
    }

    public static class VendaDiaDTO {
        private LocalDate data;
        private int quantidadeVendas;
        private BigDecimal totalDinheiro;
        private BigDecimal totalCartaoPix;
        private BigDecimal totalGeral;

        public VendaDiaDTO(LocalDate data, int quantidadeVendas, BigDecimal totalDinheiro, BigDecimal totalCartaoPix, BigDecimal totalGeral) {
            this.data = data;
            this.quantidadeVendas = quantidadeVendas;
            this.totalDinheiro = totalDinheiro;
            this.totalCartaoPix = totalCartaoPix;
            this.totalGeral = totalGeral;
        }

        public LocalDate getData() { return data; }
        public int getQuantidadeVendas() { return quantidadeVendas; }
        public BigDecimal getTotalDinheiro() { return totalDinheiro; }
        public BigDecimal getTotalCartaoPix() { return totalCartaoPix; }
        public BigDecimal getTotalGeral() { return totalGeral; }
    }

    public static class TopProdutoDTO {
        private String nome;
        private String codigoBarras;
        private String categoria;
        private int quantidadeVendida;
        private BigDecimal totalFaturado;

        public TopProdutoDTO(String nome, String codigoBarras, String categoria, int quantidadeVendida, BigDecimal totalFaturado) {
            this.nome = nome;
            this.codigoBarras = codigoBarras;
            this.categoria = categoria;
            this.quantidadeVendida = quantidadeVendida;
            this.totalFaturado = totalFaturado;
        }

        public String getNome() { return nome; }
        public String getCodigoBarras() { return codigoBarras; }
        public String getCategoria() { return categoria; }
        public int getQuantidadeVendida() { return quantidadeVendida; }
        public BigDecimal getTotalFaturado() { return totalFaturado; }
    }

    // Getters and Setters
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }
    public String getTipoRelatorio() { return tipoRelatorio; }
    public void setTipoRelatorio(String tipoRelatorio) { this.tipoRelatorio = tipoRelatorio; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public LocalDateTime getDataHoraGeracao() { return dataHoraGeracao; }
    public void setDataHoraGeracao(LocalDateTime dataHoraGeracao) { this.dataHoraGeracao = dataHoraGeracao; }
    public String getGestorNome() { return gestorNome; }
    public void setGestorNome(String gestorNome) { this.gestorNome = gestorNome; }
    public EmpresaUtilizadora getEmpresa() { return empresa; }
    public void setEmpresa(EmpresaUtilizadora empresa) { this.empresa = empresa; }
    public BigDecimal getTotalVendasValor() { return totalVendasValor; }
    public void setTotalVendasValor(BigDecimal totalVendasValor) { this.totalVendasValor = totalVendasValor; }
    public int getQuantidadeVendas() { return quantidadeVendas; }
    public void setQuantidadeVendas(int quantidadeVendas) { this.quantidadeVendas = quantidadeVendas; }
    public BigDecimal getTicketMedio() { return ticketMedio; }
    public void setTicketMedio(BigDecimal ticketMedio) { this.ticketMedio = ticketMedio; }
    public int getTotalItensVendidos() { return totalItensVendidos; }
    public void setTotalItensVendidos(int totalItensVendidos) { this.totalItensVendidos = totalItensVendidos; }
    public BigDecimal getTotalDinheiro() { return totalDinheiro; }
    public void setTotalDinheiro(BigDecimal totalDinheiro) { this.totalDinheiro = totalDinheiro; }
    public BigDecimal getTotalPix() { return totalPix; }
    public void setTotalPix(BigDecimal totalPix) { this.totalPix = totalPix; }
    public BigDecimal getTotalDebito() { return totalDebito; }
    public void setTotalDebito(BigDecimal totalDebito) { this.totalDebito = totalDebito; }
    public BigDecimal getTotalCredito() { return totalCredito; }
    public void setTotalCredito(BigDecimal totalCredito) { this.totalCredito = totalCredito; }
    public double getPercDinheiro() { return percDinheiro; }
    public void setPercDinheiro(double percDinheiro) { this.percDinheiro = percDinheiro; }
    public double getPercPix() { return percPix; }
    public void setPercPix(double percPix) { this.percPix = percPix; }
    public double getPercDebito() { return percDebito; }
    public void setPercDebito(double percDebito) { this.percDebito = percDebito; }
    public double getPercCredito() { return percCredito; }
    public void setPercCredito(double percCredito) { this.percCredito = percCredito; }
    public BigDecimal getTotalSuprimentos() { return totalSuprimentos; }
    public void setTotalSuprimentos(BigDecimal totalSuprimentos) { this.totalSuprimentos = totalSuprimentos; }
    public BigDecimal getTotalSangrias() { return totalSangrias; }
    public void setTotalSangrias(BigDecimal totalSangrias) { this.totalSangrias = totalSangrias; }
    public BigDecimal getTotalDivergencias() { return totalDivergencias; }
    public void setTotalDivergencias(BigDecimal totalDivergencias) { this.totalDivergencias = totalDivergencias; }
    public int getQuantidadeCaixas() { return quantidadeCaixas; }
    public void setQuantidadeCaixas(int quantidadeCaixas) { this.quantidadeCaixas = quantidadeCaixas; }
    public int getTotalProdutosAtivos() { return totalProdutosAtivos; }
    public void setTotalProdutosAtivos(int totalProdutosAtivos) { this.totalProdutosAtivos = totalProdutosAtivos; }
    public int getTotalUnidadesEstoque() { return totalUnidadesEstoque; }
    public void setTotalUnidadesEstoque(int totalUnidadesEstoque) { this.totalUnidadesEstoque = totalUnidadesEstoque; }
    public BigDecimal getValorTotalEstoque() { return valorTotalEstoque; }
    public void setValorTotalEstoque(BigDecimal valorTotalEstoque) { this.valorTotalEstoque = valorTotalEstoque; }
    public List<VendaHorarioDTO> getVendasPorHorario() { return vendasPorHorario; }
    public void setVendasPorHorario(List<VendaHorarioDTO> vendasPorHorario) { this.vendasPorHorario = vendasPorHorario; }
    public List<VendaCaixaDTO> getVendasPorCaixa() { return vendasPorCaixa; }
    public void setVendasPorCaixa(List<VendaCaixaDTO> vendasPorCaixa) { this.vendasPorCaixa = vendasPorCaixa; }
    public List<VendaDiaDTO> getVendasPorDia() { return vendasPorDia; }
    public void setVendasPorDia(List<VendaDiaDTO> vendasPorDia) { this.vendasPorDia = vendasPorDia; }
    public List<TopProdutoDTO> getTopProdutos() { return topProdutos; }
    public void setTopProdutos(List<TopProdutoDTO> topProdutos) { this.topProdutos = topProdutos; }
    public List<Venda> getVendasDetalhadas() { return vendasDetalhadas; }
    public void setVendasDetalhadas(List<Venda> vendasDetalhadas) { this.vendasDetalhadas = vendasDetalhadas; }
    public List<Caixa> getCaixasDetalhados() { return caixasDetalhados; }
    public void setCaixasDetalhados(List<Caixa> caixasDetalhados) { this.caixasDetalhados = caixasDetalhados; }
    public List<MovimentoCaixa> getMovimentosDetalhados() { return movimentosDetalhados; }
    public void setMovimentosDetalhados(List<MovimentoCaixa> movimentosDetalhados) { this.movimentosDetalhados = movimentosDetalhados; }
    public List<Produto> getProdutosEstoqueBaixo() { return produtosEstoqueBaixo; }
    public void setProdutosEstoqueBaixo(List<Produto> produtosEstoqueBaixo) { this.produtosEstoqueBaixo = produtosEstoqueBaixo; }
    public List<Produto> getTodosProdutos() { return todosProdutos; }
    public void setTodosProdutos(List<Produto> todosProdutos) { this.todosProdutos = todosProdutos; }
}
