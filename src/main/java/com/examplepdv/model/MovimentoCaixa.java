package com.examplepdv.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentos_caixa")
public class MovimentoCaixa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caixa_id", nullable = false)
    private Caixa caixa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentoCaixa tipo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    private String motivo;

    @Column(nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    public MovimentoCaixa() {}

    public MovimentoCaixa(Caixa caixa, TipoMovimentoCaixa tipo, BigDecimal valor, String motivo) {
        this.caixa = caixa;
        this.tipo = tipo;
        this.valor = valor;
        this.motivo = motivo;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Caixa getCaixa() {
        return caixa;
    }

    public void setCaixa(Caixa caixa) {
        this.caixa = caixa;
    }

    public TipoMovimentoCaixa getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentoCaixa tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
