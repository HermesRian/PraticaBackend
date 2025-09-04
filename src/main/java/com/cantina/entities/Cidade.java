package com.cantina.entities;

import lombok.Data;
import java.sql.Date;

@Data
public class Cidade {
    private Long id;
    private String nome;
    private String codigoIbge;
    private Long estadoId;
    private Boolean ativo;
    private Date dataCriacao;
    private Date ultimaModificacao;
    private Integer ddd;
}