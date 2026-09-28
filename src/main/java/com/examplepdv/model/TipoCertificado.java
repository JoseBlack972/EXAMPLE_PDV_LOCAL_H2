package com.examplepdv.model;

public enum TipoCertificado {
    A1("Certificado Digital A1 (Arquivo PFX / P12)"),
    A3("Certificado Digital A3 (Token USB / Smartcard)");

    private final String descricao;

    TipoCertificado(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
