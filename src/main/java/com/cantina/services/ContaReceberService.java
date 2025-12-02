package com.cantina.services;

import com.cantina.entities.ContaReceber;
import com.cantina.enums.StatusContaReceber;

import java.util.Date;
import java.util.List;

public interface ContaReceberService {
    ContaReceber salvar(ContaReceber contaReceber);
    void salvarLista(List<ContaReceber> contas);
    List<ContaReceber> listarTodas();
    ContaReceber buscarPorId(Long id);
    List<ContaReceber> buscarPorNotaSaidaId(Long notaSaidaId);
    ContaReceber atualizar(ContaReceber contaReceber);
    void excluir(Long id);
    void marcarComoPaga(Long id);
    void cancelar(Long id);
    void cancelarTodasPorNotaSaida(Long notaSaidaId);
}
