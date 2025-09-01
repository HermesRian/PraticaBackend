package com.cantina.services;

import com.cantina.entities.Marca;

import java.util.List;

public interface MarcaService {
    List<Marca> listarTodos();
    Marca salvar(Marca marca);
    Marca buscarPorId(Long id);
    void excluir(Long id);
    Marca atualizar(Long id, Marca marca);
}