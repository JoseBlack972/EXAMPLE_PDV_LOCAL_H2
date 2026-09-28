package com.examplepdv.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos_delivery")
public class PedidoDelivery implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String codigoPedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlataformaDelivery plataforma;

    @Column(nullable = false)
    private String clienteNome;

    private String clienteTelefone;

    @Column(nullable = false, length = 500)
    private String enderecoEntrega;

    @Column(nullable = false, length = 1000)
    private String itensDescricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal taxaEntrega = BigDecimal.ZERO;

    private String formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDelivery status = StatusDelivery.NOVO;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "motoboy_id")
    private Motoboy motoboy;

    private boolean saiuComMaquina = false;

    private String observacao;

    @Column(nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    public PedidoDelivery() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigoPedido() { return codigoPedido; }
    public void setCodigoPedido(String codigoPedido) { this.codigoPedido = codigoPedido; }
    public PlataformaDelivery getPlataforma() { return plataforma; }
    public void setPlataforma(PlataformaDelivery plataforma) { this.plataforma = plataforma; }
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }
    public String getClienteTelefone() { return clienteTelefone; }
    public void setClienteTelefone(String clienteTelefone) { this.clienteTelefone = clienteTelefone; }
    public String getEnderecoEntrega() { return enderecoEntrega; }
    public void setEnderecoEntrega(String enderecoEntrega) { this.enderecoEntrega = enderecoEntrega; }
    public String getItensDescricao() { return itensDescricao; }
    public void setItensDescricao(String itensDescricao) { this.itensDescricao = itensDescricao; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public BigDecimal getTaxaEntrega() { return taxaEntrega; }
    public void setTaxaEntrega(BigDecimal taxaEntrega) { this.taxaEntrega = taxaEntrega; }
    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }
    public StatusDelivery getStatus() { return status; }
    public void setStatus(StatusDelivery status) { this.status = status; }
    public Motoboy getMotoboy() { return motoboy; }
    public void setMotoboy(Motoboy motoboy) { this.motoboy = motoboy; }
    public boolean isSaiuComMaquina() { return saiuComMaquina; }
    public void setSaiuComMaquina(boolean saiuComMaquina) { this.saiuComMaquina = saiuComMaquina; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
