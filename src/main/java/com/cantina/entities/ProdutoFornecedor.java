package com.cantina.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoFornecedor {

    private Long id;
    private Long produtoId;
    private Long fornecedorId;
    private String codigoProd;
    private BigDecimal custo;
    private boolean ativo;
}