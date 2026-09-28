package com.examplepdv.dto;

import java.math.BigDecimal;

public class FechamentoCaixaDTO {
    private BigDecimal saldoFinalInformado;
    private String observacao;

    public FechamentoCaixaDTO() {}

    public BigDecimal getSaldoFinalInformado() {
        return saldoFinalInformado;
    }

    public void setSaldoFinalInformado(BigDecimal saldoFinalInformado) {
        this.saldoFinalInformado = saldoFinalInformado;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
