package com.cantina.services;

import com.cantina.database.ContaPagarDAO;
import com.cantina.database.NotaEntradaDAO;
import com.cantina.database.ProdutoDAO;
import com.cantina.entities.ContaPagar;
import com.cantina.entities.ItemNotaEntrada;
import com.cantina.entities.NotaEntrada;
import com.cantina.entities.Produto;
import com.cantina.enums.StatusContaPagar;
import com.cantina.enums.StatusNotaEntrada;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ContaPagarServiceImpl implements ContaPagarService {

    private final ContaPagarDAO contaPagarDAO;
    private final NotaEntradaDAO notaEntradaDAO;
    private final ProdutoDAO produtoDAO;

    public ContaPagarServiceImpl(ContaPagarDAO contaPagarDAO, NotaEntradaDAO notaEntradaDAO, ProdutoDAO produtoDAO) {
        this.contaPagarDAO = contaPagarDAO;
        this.notaEntradaDAO = notaEntradaDAO;
        this.produtoDAO = produtoDAO;
    }

    @Override
    public ContaPagar salvar(ContaPagar contaPagar) {
        contaPagarDAO.salvar(contaPagar);
        return contaPagar;
    }

    @Override
    public void salvarLista(List<ContaPagar> contas) {
        contaPagarDAO.salvarLista(contas);
    }

    @Override
    public List<ContaPagar> listarTodas() {
        return contaPagarDAO.listarTodas();
    }

    @Override
    public ContaPagar buscarPorId(Long id) {
        return contaPagarDAO.buscarPorId(id);
    }

    @Override
    public List<ContaPagar> buscarPorNotaEntradaId(Long notaEntradaId) {
        return contaPagarDAO.buscarPorNotaEntradaId(notaEntradaId);
    }

    @Override
    public ContaPagar atualizar(ContaPagar contaPagar) {
        contaPagarDAO.atualizar(contaPagar);
        return contaPagar;
    }

    @Override
    public void excluir(Long id) {
        contaPagarDAO.excluir(id);
    }

    @Override
    public void marcarComoPaga(Long id) {
        ContaPagar conta = contaPagarDAO.buscarPorId(id);

        if (conta == null) {
            throw new RuntimeException("Conta a pagar não encontrada");
        }

        if (conta.getStatus() == StatusContaPagar.PAGA) {
            throw new RuntimeException("Conta já está paga");
        }

        if (conta.getStatus() == StatusContaPagar.CANCELADA) {
            throw new RuntimeException("Conta está cancelada");
        }

        boolean isPrimeiraParcela = conta.getNotaEntradaId() != null &&
                contaPagarDAO.verificarPrimeiraContaPagaDaNota(conta.getNotaEntradaId());

        contaPagarDAO.marcarComoPaga(id, new Date());

        if (isPrimeiraParcela) {
            atualizarEstoque(conta.getNotaEntradaId());
        }

        if (conta.getNotaEntradaId() != null) {
            verificarEAtualizarStatusNota(conta.getNotaEntradaId());
        }
    }

    @Override
    public void cancelar(Long id) {
        ContaPagar conta = contaPagarDAO.buscarPorId(id);

        if (conta == null) {
            throw new RuntimeException("Conta a pagar não encontrada");
        }

        if (conta.getStatus() == StatusContaPagar.PAGA) {
            throw new RuntimeException("Não é possível cancelar uma conta já paga");
        }

        if (conta.getStatus() == StatusContaPagar.CANCELADA) {
            throw new RuntimeException("Conta já está cancelada");
        }

        if (conta.getNotaEntradaId() != null) {
            throw new RuntimeException("Contas vinculadas a notas de entrada devem ser canceladas através da nota. Use o endpoint PATCH /notas-entrada/" + conta.getNotaEntradaId() + "/cancelar");
        }

        contaPagarDAO.cancelar(id, new Date(), null);
    }

    @Override
    public void cancelarTodasPorNotaEntrada(Long notaEntradaId) {
        contaPagarDAO.cancelarTodasPorNotaEntrada(notaEntradaId, "Cancelamento da nota de entrada");
    }


    private void atualizarEstoque(Long notaEntradaId) {
        NotaEntrada nota = notaEntradaDAO.buscarPorId(notaEntradaId);

        if (nota == null || nota.getItens() == null || nota.getItens().isEmpty()) {
            return;
        }

        for (ItemNotaEntrada item : nota.getItens()) {
            if (item.getProdutoId() != null) {
                Produto produto = produtoDAO.buscarPorId(item.getProdutoId());

                if (produto != null) {
                    Integer quantidadeAtual = produto.getQuantidadeEstoque() != null ?
                        produto.getQuantidadeEstoque() : 0;
                    Integer quantidadeAdicionar = item.getQuantidade() != null ?
                        item.getQuantidade().intValue() : 0;
                    Integer novaQuantidade = quantidadeAtual + quantidadeAdicionar;

                    produtoDAO.atualizarEstoque(produto.getId(), novaQuantidade);
                }
            }
        }
    }

    private void verificarEAtualizarStatusNota(Long notaEntradaId) {
        List<ContaPagar> todasContas = contaPagarDAO.buscarPorNotaEntradaId(notaEntradaId);

        if (todasContas.isEmpty()) {
            return;
        }

        boolean todasPagas = todasContas.stream()
                .allMatch(conta -> conta.getStatus() == StatusContaPagar.PAGA);

        if (todasPagas) {
            notaEntradaDAO.atualizarStatus(notaEntradaId, StatusNotaEntrada.PAGA.name());
        }
    }
}