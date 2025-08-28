package com.cantina.entities;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

@Data
public class CondicaoPagamento {
    private Long id;
    private String descricao;
    private Integer dias;
    private Integer parcelas;
    private Boolean ativo;
    private Double jurosPercentual;
    private Double multaPercentual;
    private Double descontoPercentual;

    @JsonManagedReference
    private List<ParcelaCondicaoPagamento> parcelasCondicao;
}