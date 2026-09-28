package com.examplepdv.dto;

import com.examplepdv.model.TipoMovimentoCaixa;
import java.math.BigDecimal;

public class MovimentoCaixaDTO {
    private TipoMovimentoCaixa tipo;
    private BigDecimal valor;
    private String motivo;

    public MovimentoCaixaDTO() {}

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
}
