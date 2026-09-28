package com.examplepdv.model;

public enum AmbienteSefaz {
    HOMOLOGACAO("Homologação (Ambiente de Testes SEFAZ)"),
    PRODUCAO("Produção (Emissão com Valor Fiscal Real)");

    private final String descricao;

    AmbienteSefaz(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
