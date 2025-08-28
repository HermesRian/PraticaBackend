package com.cantina.entities;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovimentacaoNfe {

    private Long id;
    private Long notaFiscalId;
    private LocalDateTime dataMovimentacao;
    private String status;
    private String descricao;
}