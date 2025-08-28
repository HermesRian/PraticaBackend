package com.cantina.entities;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class NotaFiscal {
    private Long id;
    private Date dataEmissao;
    private BigDecimal valorTotal;
    private CondicaoPagamento condicaoPagamento;
    private FormaPagamento formaPagamento;
    private Fornecedor fornecedor;
    private String numero;
    private String serie;
    private String chaveAcesso;
    private Cliente cliente;
    private Transportadora transportadora;
    private Veiculo veiculo;
    private ModalidadeNfe modalidade;
    private Boolean cancelada;
    private List<ItemNotaFiscal> itensNotaFiscal;
    }