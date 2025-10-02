package com.cantina.entities;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class Transportadora {

    private Long id;
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private String email;
    private String telefone;
    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private Long cidadeId;
    private String cep;
    private String tipo;
    private String rgInscricaoEstadual;
    private Long condicaoPagamentoId;
    private String observacao;
    private Boolean ativo;
    private Timestamp dataCriacao;
    private Timestamp ultimaModificacao;
}
