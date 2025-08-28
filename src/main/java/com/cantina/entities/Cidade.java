package com.cantina.entities;


import lombok.Data;

@Data
public class Cidade {

    private Long id;
    private String nome;
    private String codigoIbge;
    private Long estadoId;


}