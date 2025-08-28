package com.cantina.entities;

import lombok.Data;

@Data
public class TranspItem {

    private Long id;
    private String codigo;
    private String descricao;
    private Long transportadoraId;
    private String codigoTransp;
    private boolean ativo;
}