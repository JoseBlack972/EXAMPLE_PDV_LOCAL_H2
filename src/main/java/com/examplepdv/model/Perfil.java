package com.examplepdv.model;

public enum Perfil {
    ADMIN("Administrador"),
    GESTOR("Gestor / Gerente"),
    OPERADOR("Operador de Caixa");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
