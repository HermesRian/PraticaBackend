package com.cantina.services;

import com.cantina.database.NotaEntradaDAO;
import com.cantina.entities.NotaEntrada;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotaEntradaServiceImpl implements NotaEntradaService {

    private final NotaEntradaDAO notaEntradaDAO;

    public NotaEntradaServiceImpl(NotaEntradaDAO notaEntradaDAO) {
        this.notaEntradaDAO = notaEntradaDAO;
    }

    @Override
    public NotaEntrada salvar(NotaEntrada notaEntrada) {
        notaEntradaDAO.salvar(notaEntrada);
        return notaEntrada;
    }

    @Override
    public List<NotaEntrada> listarTodas() {
        return notaEntradaDAO.listarTodas();
    }

    @Override
    public NotaEntrada buscarPorId(Long id) {
        return notaEntradaDAO.buscarPorId(id);
    }

    @Override
    public NotaEntrada buscarPorNumero(String numero) {
        return notaEntradaDAO.buscarPorNumero(numero);
    }

    @Override
    public NotaEntrada atualizar(NotaEntrada notaEntrada) {
        notaEntradaDAO.atualizar(notaEntrada);
        return notaEntrada;
    }

    @Override
    public void excluir(Long id) {
        notaEntradaDAO.excluir(id);
    }

    @Override
    public void atualizarStatus(Long id, String novoStatus) {
        notaEntradaDAO.atualizarStatus(id, novoStatus);
    }
}
