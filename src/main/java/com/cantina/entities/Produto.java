package com.cantina.entities;

import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class Produto {
    private Long id;
    private String nome;
    private Integer quantidadeEstoque;
    private String descricao;
    private String codigo;
    private Boolean ativo;
    private Long marcaId;
    private Integer categoriaId;
    private Long unidadeMedidaId;
    private BigDecimal valorCompra;
    private BigDecimal valorVenda;
    private Integer quantidadeMinima;
    private BigDecimal percentualLucro;
    private String observacao;
    private String referencia;
    private Date dataCriacao;
    private Date ultimaModificacao;
}
