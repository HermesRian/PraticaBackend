package com.cantina.entities;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class UnidadeMedida {
    private Long id;
    private String nome;
    private Boolean ativo;
    private Timestamp dataCriacao;
    private Timestamp ultimaModificacao;
}