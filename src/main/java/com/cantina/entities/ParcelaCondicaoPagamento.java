package com.cantina.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

@Data
public class ParcelaCondicaoPagamento {
    private Long id;
    private Integer numeroParcela;
    private Integer dias;
    private Double percentual;

    @JsonBackReference
    private CondicaoPagamento condicaoPagamento;
    private FormaPagamento formaPagamento;
}