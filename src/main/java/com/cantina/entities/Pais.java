package com.cantina.entities;

import lombok.Data;
import java.sql.Date;

@Data
public class Pais {
    private Long id;
    private String nome;
    private String sigla;
    private String ddi;
    private Boolean ativo;
    private Date dataCriacao;
    private Date ultimaModificacao;
}