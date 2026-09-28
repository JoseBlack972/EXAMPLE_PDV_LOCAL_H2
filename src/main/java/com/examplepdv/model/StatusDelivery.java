package com.examplepdv.model;

public enum StatusDelivery {
    NOVO("Novo Pedido"),
    EM_PREPARO("Em Preparo"),
    SAIU_PARA_ENTREGA("Saiu para Entrega"),
    ENTREGUE("Entregue"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusDelivery(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
}
