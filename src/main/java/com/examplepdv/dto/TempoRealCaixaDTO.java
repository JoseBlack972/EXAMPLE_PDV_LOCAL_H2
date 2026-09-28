package com.examplepdv.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TempoRealCaixaDTO {
    private boolean temCaixaAberto;
    private Long caixaId;
    private String operadorNome;
    private String operadorUsername;
    private LocalDateTime dataAbertura;
    private BigDecimal saldoInicial = BigDecimal.ZERO;
    private BigDecimal totalVendido = BigDecimal.ZERO;
    private BigDecimal totalDinheiro = BigDecimal.ZERO;
    private BigDecimal totalPix = BigDecimal.ZERO;
    private BigDecimal totalDebito = BigDecimal.ZERO;
    private BigDecimal totalCredito = BigDecimal.ZERO;
    private BigDecimal totalSangrias = BigDecimal.ZERO;
    private BigDecimal totalSuprimentos = BigDecimal.ZERO;
    private BigDecimal saldoGavetaEstimado = BigDecimal.ZERO;
    private int quantidadeVendas = 0;
    private BigDecimal ticketMedio = BigDecimal.ZERO;
    private List<UltimaVendaDTO> ultimasVendas = new ArrayList<>();

    public static class UltimaVendaDTO {
        private Long id;
        private LocalDateTime dataHora;
        private BigDecimal total;
        private String formaPagamento;
        private int quantidadeItens;
        private String operador;

        public UltimaVendaDTO(Long id, LocalDateTime dataHora, BigDecimal total, String formaPagamento, int quantidadeItens, String operador) {
            this.id = id;
            this.dataHora = dataHora;
            this.total = total;
            this.formaPagamento = formaPagamento;
            this.quantidadeItens = quantidadeItens;
            this.operador = operador;
        }

        public Long getId() { return id; }
        public LocalDateTime getDataHora() { return dataHora; }
        public BigDecimal getTotal() { return total; }
        public String getFormaPagamento() { return formaPagamento; }
        public int getQuantidadeItens() { return quantidadeItens; }
        public String getOperador() { return operador; }
    }

    public boolean isTemCaixaAberto() { return temCaixaAberto; }
    public void setTemCaixaAberto(boolean temCaixaAberto) { this.temCaixaAberto = temCaixaAberto; }
    public Long getCaixaId() { return caixaId; }
    public void setCaixaId(Long caixaId) { this.caixaId = caixaId; }
    public String getOperadorNome() { return operadorNome; }
    public void setOperadorNome(String operadorNome) { this.operadorNome = operadorNome; }
    public String getOperadorUsername() { return operadorUsername; }
    public void setOperadorUsername(String operadorUsername) { this.operadorUsername = operadorUsername; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }
    public BigDecimal getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(BigDecimal saldoInicial) { this.saldoInicial = saldoInicial; }
    public BigDecimal getTotalVendido() { return totalVendido; }
    public void setTotalVendido(BigDecimal totalVendido) { this.totalVendido = totalVendido; }
    public BigDecimal getTotalDinheiro() { return totalDinheiro; }
    public void setTotalDinheiro(BigDecimal totalDinheiro) { this.totalDinheiro = totalDinheiro; }
    public BigDecimal getTotalPix() { return totalPix; }
    public void setTotalPix(BigDecimal totalPix) { this.totalPix = totalPix; }
    public BigDecimal getTotalDebito() { return totalDebito; }
    public void setTotalDebito(BigDecimal totalDebito) { this.totalDebito = totalDebito; }
    public BigDecimal getTotalCredito() { return totalCredito; }
    public void setTotalCredito(BigDecimal totalCredito) { this.totalCredito = totalCredito; }
    public BigDecimal getTotalSangrias() { return totalSangrias; }
    public void setTotalSangrias(BigDecimal totalSangrias) { this.totalSangrias = totalSangrias; }
    public BigDecimal getTotalSuprimentos() { return totalSuprimentos; }
    public void setTotalSuprimentos(BigDecimal totalSuprimentos) { this.totalSuprimentos = totalSuprimentos; }
    public BigDecimal getSaldoGavetaEstimado() { return saldoGavetaEstimado; }
    public void setSaldoGavetaEstimado(BigDecimal saldoGavetaEstimado) { this.saldoGavetaEstimado = saldoGavetaEstimado; }
    public int getQuantidadeVendas() { return quantidadeVendas; }
    public void setQuantidadeVendas(int quantidadeVendas) { this.quantidadeVendas = quantidadeVendas; }
    public BigDecimal getTicketMedio() { return ticketMedio; }
    public void setTicketMedio(BigDecimal ticketMedio) { this.ticketMedio = ticketMedio; }
    public List<UltimaVendaDTO> getUltimasVendas() { return ultimasVendas; }
    public void setUltimasVendas(List<UltimaVendaDTO> ultimasVendas) { this.ultimasVendas = ultimasVendas; }
}
