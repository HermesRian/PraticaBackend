package com.cantina.entities;

import java.util.List;
import java.math.BigDecimal;
import java.sql.Date;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

@Data
public class CondicaoPagamento {
    private Long id;
    private String nome;
    private Integer dias;
    private Integer parcelas;
    private Boolean ativo;
    private Double jurosPercentual;
    private Double multaPercentual;
    private Double descontoPercentual;
    private Date dataCriacao;
    private Date ultimaModificacao;

    @JsonManagedReference
    private List<ParcelaCondicaoPagamento> parcelasCondicao;
}