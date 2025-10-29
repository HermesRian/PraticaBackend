package com.cantina.enums;

public enum StatusContaPagar {
    PENDENTE("Pendente"),
    PAGA("Paga"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusContaPagar(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
