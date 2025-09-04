package com.cantina.entities;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class FuncaoFuncionario {
    private Long id;
    private String descricao;
    private Boolean ativo;
    private Timestamp dataCriacao;
    private Timestamp ultimaModificacao;
    private String nome;
    private Boolean requerCnh;
    private BigDecimal cargaHoraria;
    private String observacao;
    private String userCriacao;
    private String userAtualizacao;

}
