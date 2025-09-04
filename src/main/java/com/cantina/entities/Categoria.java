package com.cantina.entities;

import lombok.Data;
import java.sql.Date;

@Data
public class Categoria {
    private Integer id;
    private String nome;
    private Boolean ativo;
    private Date dataCriacao;
    private Date ultimaModificacao;
}