package com.cantina.entities;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class ItemNotaSaida {
    private Long notaSaidaId;
    private Long produtoId;
    private Integer sequencia;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorDesconto;
    private BigDecimal percentualDesconto;
    private BigDecimal valorTotal;
    private BigDecimal rateioFrete;
    private BigDecimal rateioSeguro;
    private BigDecimal rateioOutras;
    private BigDecimal custoPrecoFinal;
    private Date dataCriacao;
    private Date ultimaModificacao;

    private Produto produto;
}
