package com.cantina.entities;

import com.cantina.enums.StatusContaPagar;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class ContaPagar {
    private Long id;

    // Dados da conta
    private String numero;
    private String modelo;
    private String serie;
    private Integer parcela;

    // Valores
    private BigDecimal valor;
    private BigDecimal desconto;
    private BigDecimal multa;
    private BigDecimal juro;
    private BigDecimal valorBaixa;

    // Relacionamentos (FKs)
    private Long fornecedorId;
    private Long formaPagamentoId;
    private Long notaEntradaId;  // Opcional

    // Datas
    private Date dataVencimento;
    private Date dataEmissao;
    private Date dataBaixa;
    private Date dataPagamento;
    private Date dataCancelamento;

    // Status
    private StatusContaPagar status;

    // Descrições
    private String descricao;
    private String justificativaCancelamento;

    // Auditoria
    private Timestamp dataCriacao;
    private Timestamp ultimaModificacao;

    // Relacionamentos (opcionais, para facilitar queries)
    private NotaEntrada notaEntrada;
    private Fornecedor fornecedor;
    private FormaPagamento formaPagamento;
}