package com.cantina.services;

import com.cantina.database.ContaPagarDAO;
import com.cantina.database.NotaEntradaDAO;
import com.cantina.database.CondicaoPagamentoDAO;
import com.cantina.entities.ContaPagar;
import com.cantina.entities.CondicaoPagamento;
import com.cantina.entities.NotaEntrada;
import com.cantina.enums.StatusContaPagar;
import com.cantina.enums.StatusNotaEntrada;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ContaPagarServiceImpl implements ContaPagarService {

    private final ContaPagarDAO contaPagarDAO;
    private final NotaEntradaDAO notaEntradaDAO;
    private final CondicaoPagamentoDAO condicaoPagamentoDAO;

    public ContaPagarServiceImpl(ContaPagarDAO contaPagarDAO, NotaEntradaDAO notaEntradaDAO, CondicaoPagamentoDAO condicaoPagamentoDAO) {
        this.contaPagarDAO = contaPagarDAO;
        this.notaEntradaDAO = notaEntradaDAO;
        this.condicaoPagamentoDAO = condicaoPagamentoDAO;
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

        Date dataPagamento = new Date();

        calcularJurosMultaDesconto(conta, dataPagamento);

        contaPagarDAO.atualizar(conta);

        contaPagarDAO.marcarComoPaga(id, dataPagamento);

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

    /**
     * Calcula juros, multa e desconto baseado na data de pagamento e condição de pagamento
     */
    private void calcularJurosMultaDesconto(ContaPagar conta, Date dataPagamento) {
        BigDecimal valorOriginal = conta.getValor();

        if (valorOriginal == null || valorOriginal.compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("Conta não possui valor definido");
        }

        CondicaoPagamento condicaoPagamento = buscarCondicaoPagamento(conta);

        if (condicaoPagamento == null) {
            conta.setDesconto(BigDecimal.ZERO);
            conta.setMulta(BigDecimal.ZERO);
            conta.setJuro(BigDecimal.ZERO);
            conta.setValorBaixa(valorOriginal);
            return;
        }

        long diffInMillies = dataPagamento.getTime() - conta.getDataVencimento().getTime();
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillies);

        BigDecimal multa = BigDecimal.ZERO;
        BigDecimal juro = BigDecimal.ZERO;
        BigDecimal desconto = BigDecimal.ZERO;

        if (diffInDays > 0) {

            if (condicaoPagamento.getMultaPercentual() != null && condicaoPagamento.getMultaPercentual() > 0) {
                multa = valorOriginal
                    .multiply(BigDecimal.valueOf(condicaoPagamento.getMultaPercentual()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            }

            if (condicaoPagamento.getJurosPercentual() != null && condicaoPagamento.getJurosPercentual() > 0) {
                double mesesAtraso = diffInDays / 30.0;

                juro = valorOriginal
                    .multiply(BigDecimal.valueOf(condicaoPagamento.getJurosPercentual()))
                    .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(mesesAtraso))
                    .setScale(2, RoundingMode.HALF_UP);
            }

        } else if (diffInDays < 0) {

            if (condicaoPagamento.getDescontoPercentual() != null && condicaoPagamento.getDescontoPercentual() > 0) {
                desconto = valorOriginal
                    .multiply(BigDecimal.valueOf(condicaoPagamento.getDescontoPercentual()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            }
        }

        conta.setMulta(multa);
        conta.setJuro(juro);
        conta.setDesconto(desconto);

        BigDecimal valorBaixa = valorOriginal
            .add(multa)
            .add(juro)
            .subtract(desconto);

        conta.setValorBaixa(valorBaixa);
    }

    private CondicaoPagamento buscarCondicaoPagamento(ContaPagar conta) {
        if (conta.getNotaEntradaId() == null) {
            return null;
        }

        NotaEntrada nota = notaEntradaDAO.buscarPorId(conta.getNotaEntradaId());

        if (nota == null || nota.getCondicaoPagamentoId() == null) {
            return null;
        }

        return condicaoPagamentoDAO.buscarPorId(nota.getCondicaoPagamentoId());
    }

    /**
     * Verifica se todas as parcelas foram pagas e atualiza o status da nota para PAGA
     */
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