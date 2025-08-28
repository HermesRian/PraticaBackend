package com.cantina.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemNotaFiscal {
    private Long id;
    private int quantidade;
    private NotaFiscal notaFiscal;
    private Produto produto;
    private String descricao;
    private BigDecimal valor;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
}