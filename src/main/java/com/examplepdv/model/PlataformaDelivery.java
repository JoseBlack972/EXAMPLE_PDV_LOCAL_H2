package com.examplepdv.model;

public enum PlataformaDelivery {
    IFOOD("iFood"),
    NOVENOVE_FOOD("99Food"),
    WHATSAPP("WhatsApp / Balcão"),
    PROPRIO("Aplicativo Próprio");

    private final String descricao;

    PlataformaDelivery(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
}
