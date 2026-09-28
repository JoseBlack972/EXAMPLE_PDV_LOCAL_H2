package com.examplepdv.model;

public enum TipoMovimentoCaixa {
    SUPRIMENTO("Suprimento / Aporte (Entrada)"),
    SANGRIA("Sangria / Retirada (Saída)"),
    VENDA("Venda em Dinheiro (Entrada)");

    private final String descricao;

    TipoMovimentoCaixa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
