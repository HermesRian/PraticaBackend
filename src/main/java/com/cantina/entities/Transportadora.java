package com.cantina.entities;

import lombok.Data;

@Data
public class Transportadora {

    private Long id;
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private String email;
    private String telefone;
    private String endereco;
    private Long cidadeId;
    private boolean ativo;
}