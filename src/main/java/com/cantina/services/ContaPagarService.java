package com.cantina.services;

import com.cantina.entities.ContaPagar;
import com.cantina.enums.StatusContaPagar;

import java.util.Date;
import java.util.List;

public interface ContaPagarService {
    ContaPagar salvar(ContaPagar contaPagar);
    void salvarLista(List<ContaPagar> contas);
    List<ContaPagar> listarTodas();
    ContaPagar buscarPorId(Long id);
    List<ContaPagar> buscarPorNotaEntradaId(Long notaEntradaId);
    ContaPagar atualizar(ContaPagar contaPagar);
    void excluir(Long id);
    void marcarComoPaga(Long id);
    void cancelar(Long id);
    void cancelarTodasPorNotaEntrada(Long notaEntradaId);
}