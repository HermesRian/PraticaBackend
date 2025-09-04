package com.cantina.entities;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Cliente {

    private Long id;
    private String nome;
    private String cnpjCpf;
    private String endereco;
    private String telefone;
    private String email;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;
    private Long cidadeId;
    private Boolean ativo;
    private String apelido;
    private BigDecimal limiteCredito;
    private String nacionalidade;
    private String rgInscricaoEstadual;
    private Date dataNascimento;
    private String estadoCivil;
    private Integer tipo;
    private String sexo;
    private Long condicaoPagamentoId;
    private String observacao;
    private Date dataCadastro;
    private Date ultimaModificacao;


}