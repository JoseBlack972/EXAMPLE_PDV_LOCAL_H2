package com.examplepdv.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "caixas")
public class Caixa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime dataAbertura = LocalDateTime.now();

    private LocalDateTime dataFechamento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoInicial = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal saldoFinal;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalVendasDinheiro = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalVendasCartaoCredito = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalVendasCartaoDebito = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalVendasPix = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalSangrias = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalSuprimentos = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCaixa status = StatusCaixa.ABERTO;

    private String observacao;

    @OneToMany(mappedBy = "caixa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovimentoCaixa> movimentos = new ArrayList<>();

    public Caixa() {}

    public Caixa(Usuario usuario, BigDecimal saldoInicial, String observacao) {
        this.usuario = usuario;
        this.saldoInicial = saldoInicial != null ? saldoInicial : BigDecimal.ZERO;
        this.observacao = observacao;
        this.status = StatusCaixa.ABERTO;
        this.dataAbertura = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial != null ? saldoInicial : BigDecimal.ZERO;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    public BigDecimal getTotalVendasDinheiro() {
        return totalVendasDinheiro != null ? totalVendasDinheiro : BigDecimal.ZERO;
    }

    public void setTotalVendasDinheiro(BigDecimal totalVendasDinheiro) {
        this.totalVendasDinheiro = totalVendasDinheiro;
    }

    public BigDecimal getTotalVendasCartaoCredito() {
        return totalVendasCartaoCredito != null ? totalVendasCartaoCredito : BigDecimal.ZERO;
    }

    public void setTotalVendasCartaoCredito(BigDecimal totalVendasCartaoCredito) {
        this.totalVendasCartaoCredito = totalVendasCartaoCredito;
    }

    public BigDecimal getTotalVendasCartaoDebito() {
        return totalVendasCartaoDebito != null ? totalVendasCartaoDebito : BigDecimal.ZERO;
    }

    public void setTotalVendasCartaoDebito(BigDecimal totalVendasCartaoDebito) {
        this.totalVendasCartaoDebito = totalVendasCartaoDebito;
    }

    public BigDecimal getTotalVendasPix() {
        return totalVendasPix != null ? totalVendasPix : BigDecimal.ZERO;
    }

    public void setTotalVendasPix(BigDecimal totalVendasPix) {
        this.totalVendasPix = totalVendasPix;
    }

    public BigDecimal getTotalSangrias() {
        return totalSangrias != null ? totalSangrias : BigDecimal.ZERO;
    }

    public void setTotalSangrias(BigDecimal totalSangrias) {
        this.totalSangrias = totalSangrias;
    }

    public BigDecimal getTotalSuprimentos() {
        return totalSuprimentos != null ? totalSuprimentos : BigDecimal.ZERO;
    }

    public void setTotalSuprimentos(BigDecimal totalSuprimentos) {
        this.totalSuprimentos = totalSuprimentos;
    }

    public StatusCaixa getStatus() {
        return status;
    }

    public void setStatus(StatusCaixa status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<MovimentoCaixa> getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(List<MovimentoCaixa> movimentos) {
        this.movimentos = movimentos;
    }

    public BigDecimal getSaldoDinheiroEsperado() {
        BigDecimal ini = saldoInicial != null ? saldoInicial : BigDecimal.ZERO;
        BigDecimal vd = totalVendasDinheiro != null ? totalVendasDinheiro : BigDecimal.ZERO;
        BigDecimal sup = totalSuprimentos != null ? totalSuprimentos : BigDecimal.ZERO;
        BigDecimal sang = totalSangrias != null ? totalSangrias : BigDecimal.ZERO;
        return ini.add(vd).add(sup).subtract(sang);
    }

    public BigDecimal getTotalVendasCartoesEPix() {
        BigDecimal cc = totalVendasCartaoCredito != null ? totalVendasCartaoCredito : BigDecimal.ZERO;
        BigDecimal cd = totalVendasCartaoDebito != null ? totalVendasCartaoDebito : BigDecimal.ZERO;
        BigDecimal pix = totalVendasPix != null ? totalVendasPix : BigDecimal.ZERO;
        return cc.add(cd).add(pix);
    }

    public BigDecimal getDiferenca() {
        if (saldoFinal == null) return null;
        return saldoFinal.subtract(getSaldoDinheiroEsperado());
    }

    public BigDecimal getSaldoFinalCalculado() {
        return getSaldoDinheiroEsperado();
    }

    public BigDecimal getSaldoFinalInformado() {
        return saldoFinal;
    }
}
