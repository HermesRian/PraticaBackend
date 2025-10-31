package com.cantina.entities;

import com.cantina.enums.StatusContaPagar;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class ContaPagar {
    private Long id;

    private String numero;
    private String modelo;
    private String serie;
    private Integer parcela;

    private BigDecimal valor;
    private BigDecimal desconto;
    private BigDecimal multa;
    private BigDecimal juro;
    private BigDecimal valorBaixa;

    private Long fornecedorId;
    private Long formaPagamentoId;
    private Long notaEntradaId;

    private Date dataVencimento;
    private Date dataEmissao;
    private Date dataBaixa;
    private Date dataPagamento;
    private Date dataCancelamento;

    private StatusContaPagar status;

    private String descricao;
    private String justificativaCancelamento;

    private Timestamp dataCriacao;
    private Timestamp ultimaModificacao;

    private NotaEntrada notaEntrada;
    private Fornecedor fornecedor;
    private FormaPagamento formaPagamento;
}