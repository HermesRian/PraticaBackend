package com.cantina.entities;

import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Produto {
    private Long id;
    private String nome;
    private BigDecimal preco;
    private Integer quantidadeEstoque;
    private String descricao;
    private String codigo;
    private Boolean ativo;
    private Long marcaId;
    private Long unidadeMedidaId;
    private BigDecimal valorCompra;
    private BigDecimal valorVenda;
    private Integer quantidadeMinima;
    private BigDecimal percentualLucro;
    private String observacoes;
    private Timestamp dataCriacao;
    private Timestamp ultimaModificacao;
}