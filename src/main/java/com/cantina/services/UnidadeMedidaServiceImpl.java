package com.cantina.services;

import com.cantina.database.UnidadeMedidaDAO;
import com.cantina.entities.UnidadeMedida;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnidadeMedidaServiceImpl implements UnidadeMedidaService {

    private final UnidadeMedidaDAO unidadeMedidaDAO = new UnidadeMedidaDAO();

    @Override
    public List<UnidadeMedida> listarTodos() {
        return unidadeMedidaDAO.listarTodos();
    }

    @Override
    public UnidadeMedida salvar(UnidadeMedida unidadeMedida) {
        unidadeMedidaDAO.salvar(unidadeMedida);
        return unidadeMedida;
    }

    @Override
    public UnidadeMedida buscarPorId(Long id) {
        return unidadeMedidaDAO.buscarPorId(id);
    }

    @Override
    public void excluir(Long id) {
        unidadeMedidaDAO.excluir(id);
    }

    @Override
    public UnidadeMedida atualizar(Long id, UnidadeMedida unidadeMedida) {
        UnidadeMedida unidadeMedidaExistente = unidadeMedidaDAO.buscarPorId(id);
        if (unidadeMedidaExistente != null) {
            unidadeMedida.setId(id);
            unidadeMedidaDAO.atualizar(unidadeMedida);
            return unidadeMedida;
        }
        return null;
    }
}