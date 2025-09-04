package com.cantina.services;

import com.cantina.database.FormaPagamentoDAO;
import com.cantina.entities.FormaPagamento;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormaPagamentoServiceImpl implements FormaPagamentoService {

    private final FormaPagamentoDAO formaPagamentoDAO = new FormaPagamentoDAO();

    @Override
    public List<FormaPagamento> listarTodos() {

        return formaPagamentoDAO.listarTodos();
    }

    @Override
    public FormaPagamento salvar(FormaPagamento formaPagamento) {
        formaPagamentoDAO.salvar(formaPagamento);
        return formaPagamento;
    }

    @Override
    public FormaPagamento buscarPorId(Long id) {

        return formaPagamentoDAO.buscarPorId(id);
    }

    @Override
    public FormaPagamento atualizar(Long id, FormaPagamento formaPagamento) {
        FormaPagamento formaPagamentoExistente = formaPagamentoDAO.buscarPorId(id);
        if (formaPagamentoExistente != null) {
            formaPagamentoExistente.setAtivo(formaPagamento.getAtivo());

            formaPagamentoDAO.atualizar(formaPagamentoExistente);
        }
        return formaPagamentoExistente;
    }

    @Override
    public void excluir(Long id) {

        formaPagamentoDAO.excluir(id);
    }
}