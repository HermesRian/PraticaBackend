package com.cantina.entities;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class Funcionario {
    private Long id;
    private String nome;
    private String cargo;
    private BigDecimal salario;
    private String email;
    private String telefone;
    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;
    private Long cidadeId;
    private Boolean ativo;
    private Date dataAdmissao;
    private Date dataDemissao;
    private String apelido;
    private Timestamp dataCriacao;
    private Timestamp dataAlteracao;
    private String rgInscricaoEstadual;
    private String cnh;
    private Date dataValidadeCnh;
    private Integer sexo;
    private String observacao;
    private Integer estadoCivil;
    private Integer isBrasileiro;
    private Integer nacionalidade;
    private Date dataNascimento;
    private Long funcaoFuncionarioId;
    private String cpfCnpj;
}