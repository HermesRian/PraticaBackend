package com.cantina.services;

import com.cantina.database.CategoriaDAO;
import com.cantina.entities.Categoria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @Override
    public List<Categoria> listarTodos() {
        return categoriaDAO.listarTodos();
    }

    @Override
    public Categoria salvar(Categoria categoria) {
        categoriaDAO.salvar(categoria);
        return categoria;
    }

    @Override
    public Categoria buscarPorId(Integer id) {
        return categoriaDAO.buscarPorId(id);
    }

    @Override
    public void excluir(Integer id) {
        categoriaDAO.excluir(id);
    }

    @Override
    public Categoria atualizar(Integer id, Categoria categoria) {
        Categoria categoriaExistente = categoriaDAO.buscarPorId(id);
        if (categoriaExistente != null) {
            categoria.setId(id);
            categoriaDAO.atualizar(categoria);
            return categoria;
        }
        return null;
    }
}