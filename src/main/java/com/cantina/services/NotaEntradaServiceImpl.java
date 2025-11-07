package com.cantina.services;

import com.cantina.database.CondicaoPagamentoDAO;
import com.cantina.database.ContaPagarDAO;
import com.cantina.database.NotaEntradaDAO;
import com.cantina.entities.CondicaoPagamento;
import com.cantina.entities.ContaPagar;
import com.cantina.entities.NotaEntrada;
import com.cantina.entities.ParcelaCondicaoPagamento;
import com.cantina.enums.StatusContaPagar;
import com.cantina.enums.StatusNotaEntrada;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class NotaEntradaServiceImpl implements NotaEntradaService {

    private final NotaEntradaDAO notaEntradaDAO;
    private final CondicaoPagamentoDAO condicaoPagamentoDAO;
    private final ContaPagarDAO contaPagarDAO;

    public NotaEntradaServiceImpl(NotaEntradaDAO notaEntradaDAO, CondicaoPagamentoDAO condicaoPagamentoDAO, ContaPagarDAO contaPagarDAO) {
        this.notaEntradaDAO = notaEntradaDAO;
        this.condicaoPagamentoDAO = condicaoPagamentoDAO;
        this.contaPagarDAO = contaPagarDAO;
    }

    @Override
    public NotaEntrada salvar(NotaEntrada notaEntrada) {
        notaEntradaDAO.salvar(notaEntrada);

        if (notaEntrada.getCondicaoPagamentoId() != null) {
            CondicaoPagamento condicaoPagamento = condicaoPagamentoDAO.buscarPorId(notaEntrada.getCondicaoPagamentoId());

            if (condicaoPagamento != null &&
                condicaoPagamento.getParcelasCondicao() != null &&
                !condicaoPagamento.getParcelasCondicao().isEmpty()) {

                List<ContaPagar> contasAPagar = gerarContasPagar(notaEntrada, condicaoPagamento);
                contaPagarDAO.salvarLista(contasAPagar);
            }
        }

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
    public void excluir(Long id)
    {
        notaEntradaDAO.excluir(id);
    }

    @Override
    public void atualizarStatus(Long id, String novoStatus) {
        notaEntradaDAO.atualizarStatus(id, novoStatus);
    }

    @Override
    public void cancelarNota(Long id) {
        NotaEntrada nota = notaEntradaDAO.buscarPorId(id);

        if (nota == null) {
            throw new RuntimeException("Nota de entrada não encontrada");
        }

        if (StatusNotaEntrada.CANCELADA.name().equals(nota.getStatus())) {
            throw new RuntimeException("Esta nota já está cancelada");
        }

        if (StatusNotaEntrada.PAGA.name().equals(nota.getStatus())) {
            throw new RuntimeException("Não é possível cancelar uma nota que já foi paga");
        }

        // Verifica se alguma parcela já foi paga
        List<ContaPagar> contas = contaPagarDAO.buscarPorNotaEntradaId(id);
        boolean algumaParcelajaPaga = contas.stream()
                .anyMatch(conta -> conta.getStatus() == StatusContaPagar.PAGA);

        if (algumaParcelajaPaga) {
            throw new RuntimeException("Não é possível cancelar a nota porque uma ou mais parcelas já foram pagas");
        }

        // Cancela todas as contas a pagar desta nota que ainda não foram pagas
        contaPagarDAO.cancelarTodasPorNotaEntrada(id, "Nota de entrada cancelada");

        // Atualiza o status da nota para CANCELADA
        notaEntradaDAO.atualizarStatus(id, StatusNotaEntrada.CANCELADA.name());
    }


    private List<ContaPagar> gerarContasPagar(NotaEntrada nota, CondicaoPagamento condicaoPagamento) {
        List<ContaPagar> contas = new ArrayList<>();
        List<ParcelaCondicaoPagamento> parcelas = condicaoPagamento.getParcelasCondicao();

        BigDecimal valorTotalNota = nota.getValorTotal();

        for (ParcelaCondicaoPagamento parcela : parcelas) {
            ContaPagar conta = new ContaPagar();

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

            conta.setFornecedorId(nota.getFornecedorId());
            conta.setFormaPagamentoId(parcela.getFormaPagamento() != null ? parcela.getFormaPagamento().getId() : null);
            conta.setNotaEntradaId(nota.getId());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(nota.getDataEmissao() != null ? nota.getDataEmissao() : new Date());
            calendar.add(Calendar.DAY_OF_MONTH, parcela.getDias());
            conta.setDataVencimento(calendar.getTime());
            conta.setDataEmissao(nota.getDataEmissao());
            conta.setDataBaixa(null);
            conta.setDataPagamento(null);
            conta.setDataCancelamento(null);

            conta.setStatus(StatusContaPagar.PENDENTE);

            conta.setDescricao("Parcela " + parcela.getNumeroParcela() + " referente à nota " + nota.getNumero());
            conta.setJustificativaCancelamento(null);

            contas.add(conta);
        }

        return contas;
    }
}
