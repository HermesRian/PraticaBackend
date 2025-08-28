package com.cantina.entities;

import lombok.Data;

@Data
public class Estado {

    private Long id;
    private String nome;
    private String uf;
    private String paisId;
}