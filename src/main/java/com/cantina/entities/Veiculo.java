package com.cantina.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Veiculo {

    private Long id;
    private String placa;
    private String modelo;
    private String marca;
    private Integer ano;
    private BigDecimal capacidade;
    private Long transportadoraId;
    private boolean ativo;
}