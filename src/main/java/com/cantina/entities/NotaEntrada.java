package com.cantina.entities;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class NotaEntrada {
    private Long id;
    private String numero;
    private String codigo;
    private String modelo;
    private String serie;

    private Long fornecedorId;
    private Fornecedor fornecedor;

    private Date dataEmissao;
    private Date dataChegada;
    private Date dataRecebimento;

    private Long condicaoPagamentoId;
    private CondicaoPagamento condicaoPagamento;

    private String status;

    private String tipoFrete;
    private Long transportadoraId;
    private Transportadora transportadora;

    private BigDecimal valorFrete;
    private BigDecimal valorSeguro;
    private BigDecimal outrasDespesas;
    private BigDecimal valorDesconto;
    private BigDecimal valorProdutos;
    private BigDecimal valorTotal;

    private String observacoes;
    private Boolean ativo;

    private Date dataCriacao;
    private Date ultimaModificacao;

    private List<ItemNotaEntrada> itens;
}
