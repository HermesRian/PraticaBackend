package com.cantina.services;

import com.cantina.database.TransportadoraDAO;
import com.cantina.entities.Transportadora;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportadoraServiceImpl implements TransportadoraService {

    private final TransportadoraDAO transportadoraDAO;

    public TransportadoraServiceImpl(TransportadoraDAO transportadoraDAO) {
        this.transportadoraDAO = transportadoraDAO;
    }

    @Override
    public Transportadora salvar(Transportadora transportadora) {
        transportadoraDAO.salvar(transportadora);
        return transportadora;
    }

    @Override
    public List<Transportadora> listarTodas() {
        return transportadoraDAO.listarTodas();
    }

    @Override
    public Transportadora buscarPorId(Long id) {
        return transportadoraDAO.buscarPorId(id);
    }

    @Override
    public Transportadora atualizar(Transportadora transportadora) {
        transportadoraDAO.atualizar(transportadora);
        return transportadora;
    }

    @Override
    public void excluir(Long id) {
        transportadoraDAO.excluir(id);
    }
}
