package com.cantina.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ParcelaCondicaoPagamento {
    private Long id;
    private Integer numeroParcela;
    private Integer dias;
    private Double percentual;
    private Long condicaoPagamentoId;
    private Long formaPagamentoId;
    private Date dataVencimento;
    private String situacao;
    private Date dataCriacao;
    private Date ultimaModificacao;

    @JsonBackReference
    private CondicaoPagamento condicaoPagamento;
    private FormaPagamento formaPagamento;
}
