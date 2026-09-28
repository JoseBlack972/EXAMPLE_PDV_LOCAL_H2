package com.examplepdv.service;

import com.examplepdv.dto.ItemVendaDTO;
import com.examplepdv.dto.VendaDTO;
import com.examplepdv.model.*;
import com.examplepdv.repository.CaixaRepository;
import com.examplepdv.repository.MovimentoCaixaRepository;
import com.examplepdv.repository.ProdutoRepository;
import com.examplepdv.repository.VendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final CaixaRepository caixaRepository;
    private final MovimentoCaixaRepository movimentoCaixaRepository;
    private final CaixaService caixaService;

    public VendaService(VendaRepository vendaRepository,
                        ProdutoRepository produtoRepository,
                        CaixaRepository caixaRepository,
                        MovimentoCaixaRepository movimentoCaixaRepository,
                        CaixaService caixaService) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
        this.caixaRepository = caixaRepository;
        this.movimentoCaixaRepository = movimentoCaixaRepository;
        this.caixaService = caixaService;
    }

    public List<Venda> listarTodas() {
        return vendaRepository.findAllByOrderByIdDesc();
    }

    public Optional<Venda> buscarPorId(Long id) {
        return vendaRepository.findById(id);
    }

    @Transactional
    public Venda realizarVenda(VendaDTO dto, Usuario usuarioLogado) {
        Caixa caixa = caixaService.obterCaixaAberto()
                .orElseThrow(() -> new IllegalStateException("O caixa deve estar ABERTO para realizar vendas!"));

        if (dto.getItens() == null || dto.getItens().isEmpty()) {
            throw new IllegalArgumentException("A venda deve conter pelo menos um item!");
        }

        Venda venda = new Venda();
        venda.setUsuario(usuarioLogado);
        venda.setCaixa(caixa);
        venda.setDataHora(LocalDateTime.now());
        venda.setFormaPagamento(dto.getFormaPagamento());
        venda.setStatus(StatusVenda.FINALIZADA);
        venda.setCpfCnpj(dto.getCpfCnpj());

        BigDecimal total = BigDecimal.ZERO;

        for (ItemVendaDTO itemDto : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado ID: " + itemDto.getProdutoId()));

            if (!produto.isAtivo()) {
                throw new IllegalArgumentException("O produto " + produto.getNome() + " está inativo!");
            }

            if (produto.getEstoque() < itemDto.getQuantidade()) {
                throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getNome() + 
                                                   " (Disponível: " + produto.getEstoque() + ")");
            }

            // Atualiza estoque
            produto.setEstoque(produto.getEstoque() - itemDto.getQuantidade());
            produtoRepository.save(produto);

            // Cria item da venda
            ItemVenda itemVenda = new ItemVenda(produto, itemDto.getQuantidade(), produto.getPreco());
            venda.adicionarItem(itemVenda);

            total = total.add(itemVenda.getSubtotal());
        }

        venda.setValorTotal(total);

        // Tratamento de troco para dinheiro
        if (dto.getFormaPagamento() == FormaPagamento.DINHEIRO) {
            BigDecimal recebido = dto.getValorRecebido() != null ? dto.getValorRecebido() : total;
            if (recebido.compareTo(total) < 0) {
                throw new IllegalArgumentException("Valor recebido (R$ " + recebido + ") é menor que o total da venda (R$ " + total + ")");
            }
            venda.setValorRecebido(recebido);
            venda.setTroco(recebido.subtract(total));

            // Atualiza totais do caixa
            caixa.setTotalVendasDinheiro(caixa.getTotalVendasDinheiro().add(total));
            caixaRepository.save(caixa);

            // Registra movimento de caixa para dinheiro
            MovimentoCaixa movimento = new MovimentoCaixa(
                    caixa,
                    TipoMovimentoCaixa.VENDA,
                    total,
                    "Venda PDV #" + venda.getId()
            );
            movimentoCaixaRepository.save(movimento);
        } else if (dto.getFormaPagamento() == FormaPagamento.CARTAO_CREDITO) {
            caixa.setTotalVendasCartaoCredito(caixa.getTotalVendasCartaoCredito().add(total));
            caixaRepository.save(caixa);
        } else if (dto.getFormaPagamento() == FormaPagamento.CARTAO_DEBITO) {
            caixa.setTotalVendasCartaoDebito(caixa.getTotalVendasCartaoDebito().add(total));
            caixaRepository.save(caixa);
        } else if (dto.getFormaPagamento() == FormaPagamento.PIX) {
            caixa.setTotalVendasPix(caixa.getTotalVendasPix().add(total));
            caixaRepository.save(caixa);
        }

        return vendaRepository.save(venda);
    }
}
