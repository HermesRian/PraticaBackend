package com.cantina.enums;

public enum StatusContaReceber {
    PENDENTE("Pendente"),
    PAGA("Paga"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusContaReceber(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
