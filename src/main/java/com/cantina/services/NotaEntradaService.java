package com.cantina.services;

import com.cantina.entities.NotaEntrada;
import java.util.List;

public interface NotaEntradaService {
    NotaEntrada salvar(NotaEntrada notaEntrada);
    List<NotaEntrada> listarTodas();
    NotaEntrada buscarPorId(Long id);
    NotaEntrada buscarPorNumero(String numero);
    NotaEntrada atualizar(NotaEntrada notaEntrada);
    void excluir(Long id);
    void atualizarStatus(Long id, String novoStatus);
}
