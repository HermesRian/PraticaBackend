package com.cantina.entities;

import com.cantina.enums.StatusContaReceber;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class ContaReceber {
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

    private Long clienteId;
    private Long formaPagamentoId;
    private Long notaSaidaId;

    private Date dataVencimento;
    private Date dataEmissao;
    private Date dataBaixa;
    private Date dataPagamento;
    private Date dataCancelamento;

    private StatusContaReceber status;

    private String descricao;
    private String justificativaCancelamento;

    private Timestamp dataCriacao;
    private Timestamp ultimaModificacao;

    private NotaSaida notaSaida;
    private Cliente cliente;
    private FormaPagamento formaPagamento;
}
