package com.cantina.entities;

import lombok.Data;

@Data
public class ModalidadeNfe {

    private Long id;
    private String codigo;
    private String descricao;
    private boolean ativo;
}