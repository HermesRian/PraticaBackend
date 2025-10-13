package com.cantina.services;

import com.cantina.entities.Transportadora;

import java.util.List;

public interface TransportadoraService {
    Transportadora salvar(Transportadora transportadora);
    List<Transportadora> listarTodas();
    Transportadora buscarPorId(Long id);
    Transportadora atualizar(Transportadora transportadora);
    void excluir(Long id);
}
