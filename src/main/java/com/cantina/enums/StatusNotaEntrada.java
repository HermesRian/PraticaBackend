package com.cantina.enums;

public enum StatusNotaEntrada {
    PENDENTE("Pendente"),
    PAGA("Paga"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusNotaEntrada(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}