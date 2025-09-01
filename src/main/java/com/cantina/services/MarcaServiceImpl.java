package com.cantina.services;

import com.cantina.database.MarcaDAO;
import com.cantina.entities.Marca;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarcaServiceImpl implements MarcaService {

    private final MarcaDAO marcaDAO = new MarcaDAO();

    @Override
    public List<Marca> listarTodos() {
        return marcaDAO.listarTodos();
    }

    @Override
    public Marca salvar(Marca marca) {
        marcaDAO.salvar(marca);
        return marca;
    }

    @Override
    public Marca buscarPorId(Long id) {
        return marcaDAO.buscarPorId(id);
    }

    @Override
    public void excluir(Long id) {
        marcaDAO.excluir(id);
    }

    @Override
    public Marca atualizar(Long id, Marca marca) {
        Marca marcaExistente = marcaDAO.buscarPorId(id);
        if (marcaExistente != null) {
            marca.setId(id);
            marcaDAO.atualizar(marca);
            return marca;
        }
        return null;
    }
}