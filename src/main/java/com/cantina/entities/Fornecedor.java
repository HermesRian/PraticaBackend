package com.cantina.entities;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Fornecedor {
    private Long id;
    private Integer tipo;
    private String razaoSocial;
    private String nomeFantasia;
    private String cpfCnpj;
    private String email;
    private String telefone;
    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;
    private Long cidadeId;
    private String rgInscricaoEstadual;
    private Boolean ativo;
    private Long condicaoPagamentoId;
    private BigDecimal limiteCredito;
    private String observacao;
    private Timestamp dataCriacao;
    private Timestamp ultimaModificacao;

}