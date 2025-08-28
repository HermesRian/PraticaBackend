package com.cantina.entities;

import lombok.Data;

import java.util.List;

@Data
public class FormaPagamento {
    private Long id;
    private String descricao;
    private String codigo;
    private String tipo;
    private Boolean ativo;
   // private List<NotaFiscal> notasFiscais;
}