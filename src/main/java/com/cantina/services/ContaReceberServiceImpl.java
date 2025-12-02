package com.cantina.services;

import com.cantina.database.ContaReceberDAO;
import com.cantina.database.NotaSaidaDAO;
import com.cantina.database.CondicaoPagamentoDAO;
import com.cantina.entities.ContaReceber;
import com.cantina.entities.CondicaoPagamento;
import com.cantina.entities.NotaSaida;
import com.cantina.enums.StatusContaReceber;
import com.cantina.enums.StatusNotaSaida;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ContaReceberServiceImpl implements ContaReceberService {

    private final ContaReceberDAO contaReceberDAO;
    private final NotaSaidaDAO notaSaidaDAO;
    private final CondicaoPagamentoDAO condicaoPagamentoDAO;

    public ContaReceberServiceImpl(ContaReceberDAO contaReceberDAO, NotaSaidaDAO notaSaidaDAO, CondicaoPagamentoDAO condicaoPagamentoDAO) {
        this.contaReceberDAO = contaReceberDAO;
        this.notaSaidaDAO = notaSaidaDAO;
        this.condicaoPagamentoDAO = condicaoPagamentoDAO;
    }

    @Override
    public ContaReceber salvar(ContaReceber contaReceber) {
        contaReceberDAO.salvar(contaReceber);
        return contaReceber;
    }

    @Override
    public void salvarLista(List<ContaReceber> contas) {
        contaReceberDAO.salvarLista(contas);
    }

    @Override
    public List<ContaReceber> listarTodas() {
        return contaReceberDAO.listarTodas();
    }

    @Override
    public ContaReceber buscarPorId(Long id) {
        return contaReceberDAO.buscarPorId(id);
    }

    @Override
    public List<ContaReceber> buscarPorNotaSaidaId(Long notaSaidaId) {
        return contaReceberDAO.buscarPorNotaSaidaId(notaSaidaId);
    }

    @Override
    public ContaReceber atualizar(ContaReceber contaReceber) {
        contaReceberDAO.atualizar(contaReceber);
        return contaReceber;
    }

    @Override
    public void excluir(Long id) {
        contaReceberDAO.excluir(id);
    }

    @Override
    public void marcarComoPaga(Long id) {
        ContaReceber conta = contaReceberDAO.buscarPorId(id);

        if (conta == null) {
            throw new RuntimeException("Conta a receber não encontrada");
        }

        if (conta.getStatus() == StatusContaReceber.PAGA) {
            throw new RuntimeException("Conta já está paga");
        }

        if (conta.getStatus() == StatusContaReceber.CANCELADA) {
            throw new RuntimeException("Conta está cancelada");
        }

        Date dataPagamento = new Date();

        calcularJurosMultaDesconto(conta, dataPagamento);

        contaReceberDAO.atualizar(conta);

        contaReceberDAO.marcarComoPaga(id, dataPagamento);

        if (conta.getNotaSaidaId() != null) {
            verificarEAtualizarStatusNota(conta.getNotaSaidaId());
        }
    }

    @Override
    public void cancelar(Long id) {
        ContaReceber conta = contaReceberDAO.buscarPorId(id);

        if (conta == null) {
            throw new RuntimeException("Conta a receber não encontrada");
        }

        if (conta.getStatus() == StatusContaReceber.PAGA) {
            throw new RuntimeException("Não é possível cancelar uma conta já paga");
        }

        if (conta.getStatus() == StatusContaReceber.CANCELADA) {
            throw new RuntimeException("Conta já está cancelada");
        }

        if (conta.getNotaSaidaId() != null) {
            throw new RuntimeException("Contas vinculadas a notas de saída devem ser canceladas através da nota. Use o endpoint PATCH /notas-saida/" + conta.getNotaSaidaId() + "/cancelar");
        }

        contaReceberDAO.cancelar(id, new Date(), null);
    }

    @Override
    public void cancelarTodasPorNotaSaida(Long notaSaidaId) {
        contaReceberDAO.cancelarTodasPorNotaSaida(notaSaidaId, "Cancelamento da nota de saída");
    }

    private void calcularJurosMultaDesconto(ContaReceber conta, Date dataPagamento) {
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

    private CondicaoPagamento buscarCondicaoPagamento(ContaReceber conta) {
        if (conta.getNotaSaidaId() == null) {
            return null;
        }

        NotaSaida nota = notaSaidaDAO.buscarPorId(conta.getNotaSaidaId());

        if (nota == null || nota.getCondicaoPagamentoId() == null) {
            return null;
        }

        return condicaoPagamentoDAO.buscarPorId(nota.getCondicaoPagamentoId());
    }

    private void verificarEAtualizarStatusNota(Long notaSaidaId) {
        List<ContaReceber> todasContas = contaReceberDAO.buscarPorNotaSaidaId(notaSaidaId);

        if (todasContas.isEmpty()) {
            return;
        }

        boolean todasPagas = todasContas.stream()
                .allMatch(conta -> conta.getStatus() == StatusContaReceber.PAGA);

        if (todasPagas) {
            notaSaidaDAO.atualizarStatus(notaSaidaId, StatusNotaSaida.PAGA.name());
        }
    }
}
