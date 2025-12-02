package com.cantina.services;

import com.cantina.database.NotaSaidaDAO;
import com.cantina.database.ProdutoDAO;
import com.cantina.entities.*;
import com.cantina.enums.StatusNotaSaida;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotaSaidaServiceImpl implements NotaSaidaService {

    private final NotaSaidaDAO notaSaidaDAO;
    private final ProdutoDAO produtoDAO;

    public NotaSaidaServiceImpl(NotaSaidaDAO notaSaidaDAO, ProdutoDAO produtoDAO) {
        this.notaSaidaDAO = notaSaidaDAO;
        this.produtoDAO = produtoDAO;
    }

    @Override
    public NotaSaida salvar(NotaSaida notaSaida) {
        notaSaidaDAO.salvar(notaSaida);

        darBaixaEstoque(notaSaida);

        return notaSaida;
    }

    @Override
    public List<NotaSaida> listarTodas() {
        return notaSaidaDAO.listarTodas();
    }

    @Override
    public NotaSaida buscarPorId(Long id) {
        return notaSaidaDAO.buscarPorId(id);
    }

    @Override
    public NotaSaida buscarPorNumero(String numero) {
        return notaSaidaDAO.buscarPorNumero(numero);
    }

    @Override
    public NotaSaida atualizar(NotaSaida notaSaida) {
        notaSaidaDAO.atualizar(notaSaida);
        return notaSaida;
    }

    @Override
    public void excluir(Long id) {
        notaSaidaDAO.excluir(id);
    }

    @Override
    public void atualizarStatus(Long id, String novoStatus) {
        notaSaidaDAO.atualizarStatus(id, novoStatus);
    }

    @Override
    public void cancelarNota(Long id) {
        NotaSaida nota = notaSaidaDAO.buscarPorId(id);

        if (nota == null) {
            throw new RuntimeException("Nota de saída não encontrada");
        }

        if (StatusNotaSaida.CANCELADA.name().equals(nota.getStatus())) {
            throw new RuntimeException("Esta nota já está cancelada");
        }

        if (StatusNotaSaida.PAGA.name().equals(nota.getStatus())) {
            throw new RuntimeException("Não é possível cancelar uma nota que já foi paga");
        }

        reverterBaixaEstoque(nota);

        notaSaidaDAO.atualizarStatus(id, StatusNotaSaida.CANCELADA.name());
    }

    private void darBaixaEstoque(NotaSaida nota) {
        if (nota == null || nota.getItens() == null || nota.getItens().isEmpty()) {
            return;
        }

        for (ItemNotaSaida item : nota.getItens()) {
            if (item.getProdutoId() != null) {
                Produto produto = produtoDAO.buscarPorId(item.getProdutoId());

                if (produto != null) {
                    Integer quantidadeAtual = produto.getQuantidadeEstoque() != null ? produto.getQuantidadeEstoque() : 0;
                    Integer quantidadeRemover = item.getQuantidade() != null ? item.getQuantidade().intValue() : 0;

                    if (quantidadeAtual < quantidadeRemover) {
                        throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome() +
                                ". Disponível: " + quantidadeAtual + ", Solicitado: " + quantidadeRemover);
                    }

                    Integer novaQuantidade = quantidadeAtual - quantidadeRemover;
                    produtoDAO.atualizarEstoque(produto.getId(), novaQuantidade);
                }
            }
        }
    }

    private void reverterBaixaEstoque(NotaSaida nota) {
        if (nota == null || nota.getItens() == null || nota.getItens().isEmpty()) {
            return;
        }

        for (ItemNotaSaida item : nota.getItens()) {
            if (item.getProdutoId() != null) {
                Produto produto = produtoDAO.buscarPorId(item.getProdutoId());

                if (produto != null) {
                    Integer quantidadeAtual = produto.getQuantidadeEstoque() != null ? produto.getQuantidadeEstoque() : 0;
                    Integer quantidadeAdicionar = item.getQuantidade() != null ? item.getQuantidade().intValue() : 0;
                    Integer novaQuantidade = quantidadeAtual + quantidadeAdicionar;
                    produtoDAO.atualizarEstoque(produto.getId(), novaQuantidade);
                }
            }
        }
    }
}
