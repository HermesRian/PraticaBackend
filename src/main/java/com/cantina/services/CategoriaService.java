package com.cantina.services;

import com.cantina.entities.Categoria;

import java.util.List;

public interface CategoriaService {
    List<Categoria> listarTodos();
    Categoria salvar(Categoria categoria);
    Categoria buscarPorId(Integer id);
    void excluir(Integer id);
    Categoria atualizar(Integer id, Categoria categoria);
}