package com.cantina.services;

import com.cantina.entities.UnidadeMedida;

import java.util.List;

public interface UnidadeMedidaService {
    List<UnidadeMedida> listarTodos();
    UnidadeMedida salvar(UnidadeMedida unidadeMedida);
    UnidadeMedida buscarPorId(Long id);
    void excluir(Long id);
    UnidadeMedida atualizar(Long id, UnidadeMedida unidadeMedida);
}