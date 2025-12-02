package com.cantina.enums;

public enum StatusNotaSaida {
    PENDENTE("Pendente"),
    PAGA("Paga"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusNotaSaida(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
