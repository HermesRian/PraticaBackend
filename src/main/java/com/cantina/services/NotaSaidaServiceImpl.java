package com.cantina.services;

import com.cantina.database.CondicaoPagamentoDAO;
import com.cantina.database.ContaReceberDAO;
import com.cantina.database.NotaSaidaDAO;
import com.cantina.database.ProdutoDAO;
import com.cantina.entities.*;
import com.cantina.enums.StatusContaReceber;
import com.cantina.enums.StatusNotaSaida;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class NotaSaidaServiceImpl implements NotaSaidaService {

    private final NotaSaidaDAO notaSaidaDAO;
    private final ProdutoDAO produtoDAO;
    private final CondicaoPagamentoDAO condicaoPagamentoDAO;
    private final ContaReceberDAO contaReceberDAO;

    public NotaSaidaServiceImpl(NotaSaidaDAO notaSaidaDAO, ProdutoDAO produtoDAO, CondicaoPagamentoDAO condicaoPagamentoDAO, ContaReceberDAO contaReceberDAO) {
        this.notaSaidaDAO = notaSaidaDAO;
        this.produtoDAO = produtoDAO;
        this.condicaoPagamentoDAO = condicaoPagamentoDAO;
        this.contaReceberDAO = contaReceberDAO;
    }

    @Override
    public NotaSaida salvar(NotaSaida notaSaida) {
        notaSaidaDAO.salvar(notaSaida);

        darBaixaEstoque(notaSaida);

        if (notaSaida.getCondicaoPagamentoId() != null) {
            CondicaoPagamento condicaoPagamento = condicaoPagamentoDAO.buscarPorId(notaSaida.getCondicaoPagamentoId());

            if (condicaoPagamento != null &&
                condicaoPagamento.getParcelasCondicao() != null &&
                !condicaoPagamento.getParcelasCondicao().isEmpty()) {

                List<ContaReceber> contasAReceber = gerarContasReceber(notaSaida, condicaoPagamento);
                contaReceberDAO.salvarLista(contasAReceber);
            }
        }

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

        List<ContaReceber> contas = contaReceberDAO.buscarPorNotaSaidaId(id);
        boolean algumaParcelajaPaga = contas.stream()
                .anyMatch(conta -> conta.getStatus() == StatusContaReceber.PAGA);

        if (algumaParcelajaPaga) {
            throw new RuntimeException("Não é possível cancelar a nota porque uma ou mais parcelas já foram pagas");
        }

        reverterBaixaEstoque(nota);

        contaReceberDAO.cancelarTodasPorNotaSaida(id, "Nota de saída cancelada");

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

                    if (item.getValorUnitario() != null) {
                        produtoDAO.atualizarValorVenda(produto.getId(), item.getValorUnitario());
                        produtoDAO.calcularEAtualizarPercentualLucro(produto.getId());
                    }
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

    private List<ContaReceber> gerarContasReceber(NotaSaida nota, CondicaoPagamento condicaoPagamento) {
        List<ContaReceber> contas = new ArrayList<>();
        List<ParcelaCondicaoPagamento> parcelas = condicaoPagamento.getParcelasCondicao();

        BigDecimal valorTotalNota = nota.getValorTotal();

        for (ParcelaCondicaoPagamento parcela : parcelas) {
            ContaReceber conta = new ContaReceber();

            conta.setNumero(nota.getNumero());
            conta.setModelo(nota.getModelo());
            conta.setSerie(nota.getSerie());
            conta.setParcela(parcela.getNumeroParcela());

            BigDecimal percentual = BigDecimal.valueOf(parcela.getPercentual()).divide(BigDecimal.valueOf(100));
            BigDecimal valorParcela = valorTotalNota.multiply(percentual).setScale(2, RoundingMode.HALF_UP);
            conta.setValor(valorParcela);
            conta.setDesconto(BigDecimal.ZERO);
            conta.setMulta(BigDecimal.ZERO);
            conta.setJuro(BigDecimal.ZERO);
            conta.setValorBaixa(null);

            conta.setClienteId(nota.getClienteId());
            conta.setFormaPagamentoId(parcela.getFormaPagamento() != null ? parcela.getFormaPagamento().getId() : null);
            conta.setNotaSaidaId(nota.getId());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(nota.getDataEmissao() != null ? nota.getDataEmissao() : new Date());
            calendar.add(Calendar.DAY_OF_MONTH, parcela.getDias());
            conta.setDataVencimento(calendar.getTime());
            conta.setDataEmissao(nota.getDataEmissao());
            conta.setDataBaixa(null);
            conta.setDataPagamento(null);
            conta.setDataCancelamento(null);

            conta.setStatus(StatusContaReceber.PENDENTE);

            conta.setDescricao("Parcela " + parcela.getNumeroParcela() + " referente à nota " + nota.getNumero());
            conta.setJustificativaCancelamento(null);

            contas.add(conta);
        }

        return contas;
    }
}
