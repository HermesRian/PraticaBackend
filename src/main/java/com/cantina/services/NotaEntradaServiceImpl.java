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

    @Override
    public void confirmarNota(Long id) {
        NotaEntrada nota = notaEntradaDAO.buscarPorId(id);

        if (nota == null) {
            throw new RuntimeException("Nota de entrada não encontrada");
        }

        if (!StatusNotaEntrada.PENDENTE.name().equals(nota.getStatus())) {
            throw new RuntimeException("Apenas notas com status PENDENTE podem ser confirmadas");
        }

        if (nota.getCondicaoPagamentoId() == null) {
            throw new RuntimeException("Nota de entrada não possui condição de pagamento definida");
        }

        // Busca a condição de pagamento com suas parcelas
        CondicaoPagamento condicaoPagamento = condicaoPagamentoDAO.buscarPorId(nota.getCondicaoPagamentoId());

        if (condicaoPagamento == null) {
            throw new RuntimeException("Condição de pagamento não encontrada");
        }

        if (condicaoPagamento.getParcelasCondicao() == null || condicaoPagamento.getParcelasCondicao().isEmpty()) {
            throw new RuntimeException("Condição de pagamento não possui parcelas configuradas");
        }

        // Gera as contas a pagar baseadas nas parcelas da condição de pagamento
        List<ContaPagar> contasAPagar = gerarContasPagar(nota, condicaoPagamento);

        // Salva as contas a pagar
        contaPagarDAO.salvarLista(contasAPagar);

        // Atualiza o status da nota para CONFIRMADA
        notaEntradaDAO.atualizarStatus(id, StatusNotaEntrada.CONFIRMADA.name());
    }

    @Override
    public void cancelarNota(Long id) {
        NotaEntrada nota = notaEntradaDAO.buscarPorId(id);

        if (nota == null) {
            throw new RuntimeException("Nota de entrada não encontrada");
        }

        if (StatusNotaEntrada.PENDENTE.name().equals(nota.getStatus())) {
            // Se a nota está PENDENTE, apenas cancela
            notaEntradaDAO.atualizarStatus(id, StatusNotaEntrada.CANCELADA.name());
        } else {
            throw new RuntimeException("Apenas notas PENDENTE podem ser canceladas diretamente. " +
                    "Para notas CONFIRMADAS, cancele pela conta a pagar");
        }
    }

    /**
     * Gera as contas a pagar baseadas nas parcelas da condição de pagamento
     */
    private List<ContaPagar> gerarContasPagar(NotaEntrada nota, CondicaoPagamento condicaoPagamento) {
        List<ContaPagar> contas = new ArrayList<>();
        List<ParcelaCondicaoPagamento> parcelas = condicaoPagamento.getParcelasCondicao();

        BigDecimal valorTotalNota = nota.getValorTotal();

        for (ParcelaCondicaoPagamento parcela : parcelas) {
            ContaPagar conta = new ContaPagar();

            // Dados da conta
            conta.setNumero(nota.getNumero());
            conta.setModelo(nota.getModelo());
            conta.setSerie(nota.getSerie());
            conta.setParcela(parcela.getNumeroParcela());

            // Valores
            BigDecimal percentual = BigDecimal.valueOf(parcela.getPercentual()).divide(BigDecimal.valueOf(100));
            BigDecimal valorParcela = valorTotalNota.multiply(percentual).setScale(2, RoundingMode.HALF_UP);
            conta.setValor(valorParcela);
            conta.setDesconto(BigDecimal.ZERO);
            conta.setMulta(BigDecimal.ZERO);
            conta.setJuro(BigDecimal.ZERO);
            conta.setValorBaixa(null);

            // Relacionamentos (FKs)
            conta.setFornecedorId(nota.getFornecedorId());
            conta.setFormaPagamentoId(parcela.getFormaPagamento() != null ? parcela.getFormaPagamento().getId() : null);
            conta.setNotaEntradaId(nota.getId());

            // Datas
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(nota.getDataEmissao() != null ? nota.getDataEmissao() : new Date());
            calendar.add(Calendar.DAY_OF_MONTH, parcela.getDias());
            conta.setDataVencimento(calendar.getTime());
            conta.setDataEmissao(nota.getDataEmissao());
            conta.setDataBaixa(null);
            conta.setDataPagamento(null);
            conta.setDataCancelamento(null);

            // Status
            conta.setStatus(StatusContaPagar.PENDENTE);

            // Descrições
            conta.setDescricao("Parcela " + parcela.getNumeroParcela() + " referente à nota " + nota.getNumero());
            conta.setJustificativaCancelamento(null);

            contas.add(conta);
        }

        return contas;
    }
}
