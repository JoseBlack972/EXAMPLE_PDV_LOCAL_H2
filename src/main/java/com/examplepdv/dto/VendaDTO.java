package com.examplepdv.dto;

import com.examplepdv.model.FormaPagamento;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VendaDTO {
    private FormaPagamento formaPagamento;
    private BigDecimal valorRecebido;
    private BigDecimal troco;
    private String cpfCnpj;
    private List<ItemVendaDTO> itens = new ArrayList<>();

    public VendaDTO() {}

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public BigDecimal getValorRecebido() {
        return valorRecebido;
    }

    public void setValorRecebido(BigDecimal valorRecebido) {
        this.valorRecebido = valorRecebido;
    }

    public BigDecimal getTroco() {
        return troco;
    }

    public void setTroco(BigDecimal troco) {
        this.troco = troco;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public List<ItemVendaDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemVendaDTO> itens) {
        this.itens = itens;
    }
}
