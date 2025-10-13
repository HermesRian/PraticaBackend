package com.cantina.entities;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class NotaEntrada {
    private Long id;
    private Integer numeroSequencial;
    private String numero;
    private String codigo;
    private String modelo;
    private String serie;

    private String codigoFornecedor;
    private Long fornecedorId;
    private Fornecedor fornecedor;

    private Date dataEmissao;
    private Date dataChegada;
    private Date dataEntregaRealizada;

    private Long condicaoPagamentoId;
    private Long formaPagamentoId;
    private CondicaoPagamento condicaoPagamento;
    private FormaPagamento formaPagamento;

    private Long funcionarioId;
    private String status;

    private String tipoFrete;
    private Long transportadoraId;
    private Transportadora transportadora;

    private BigDecimal valorFrete;
    private BigDecimal valorSeguro;
    private BigDecimal outrasDespesas;
    private BigDecimal valorDesconto;
    private BigDecimal valorAcrescimo;
    private BigDecimal totalProdutos;
    private BigDecimal totalAPagar;
    private BigDecimal valorProdutos;
    private BigDecimal valorTotal;

    private String observacoes;
    private Boolean ativo;

    private Date dataCriacao;
    private Date ultimaModificacao;

    private List<ItemNotaFiscal> itensNotaFiscal;
}
