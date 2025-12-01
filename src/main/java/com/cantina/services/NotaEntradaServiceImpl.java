package com.cantina.services;

import com.cantina.database.CondicaoPagamentoDAO;
import com.cantina.database.ContaPagarDAO;
import com.cantina.database.NotaEntradaDAO;
import com.cantina.database.ProdutoDAO;
import com.cantina.entities.*;
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
    private final ProdutoDAO produtoDAO;

    public NotaEntradaServiceImpl(NotaEntradaDAO notaEntradaDAO, CondicaoPagamentoDAO condicaoPagamentoDAO, ContaPagarDAO contaPagarDAO, ProdutoDAO produtoDAO) {
        this.notaEntradaDAO = notaEntradaDAO;
        this.condicaoPagamentoDAO = condicaoPagamentoDAO;
        this.contaPagarDAO = contaPagarDAO;
        this.produtoDAO = produtoDAO;
    }

    @Override
    public NotaEntrada salvar(NotaEntrada notaEntrada) {
        // Salva a nota de entrada
        notaEntradaDAO.salvar(notaEntrada);

        // Atualiza estoque e valorCompra dos produtos
        atualizarEstoqueEValorCompra(notaEntrada);

        // Se a nota tiver condição de pagamento, gera as contas a pagar automaticamente
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

        // Reverte estoque e valorCompra dos produtos
        reverterEstoqueEValorCompra(nota);

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

    /**
     * Atualiza o estoque e valor de compra dos produtos quando uma nota é criada
     */
    private void atualizarEstoqueEValorCompra(NotaEntrada nota) {
        if (nota == null || nota.getItens() == null || nota.getItens().isEmpty()) {
            return;
        }

        for (ItemNotaEntrada item : nota.getItens()) {
            if (item.getProdutoId() != null) {
                Produto produto = produtoDAO.buscarPorId(item.getProdutoId());

                if (produto != null) {
                    // Atualiza estoque
                    Integer quantidadeAtual = produto.getQuantidadeEstoque() != null ? produto.getQuantidadeEstoque() : 0;
                    Integer quantidadeAdicionar = item.getQuantidade() != null ? item.getQuantidade().intValue() : 0;
                    Integer novaQuantidade = quantidadeAtual + quantidadeAdicionar;
                    produtoDAO.atualizarEstoque(produto.getId(), novaQuantidade);

                    // Atualiza valorCompra (sempre usa o valor da nota mais recente)
                    if (item.getValorUnitario() != null) {
                        produtoDAO.atualizarValorCompra(produto.getId(), item.getValorUnitario());
                    }

                    // Calcula e atualiza o custoProduto com rateio das despesas
                    BigDecimal custoProduto = calcularCustoProdutoComRateio(nota, item);
                    produtoDAO.atualizarCustoProduto(produto.getId(), custoProduto);
                }
            }
        }
    }

    /**
     * Reverte o estoque e valor de compra dos produtos quando uma nota é cancelada
     */
    private void reverterEstoqueEValorCompra(NotaEntrada nota) {
        if (nota == null || nota.getItens() == null || nota.getItens().isEmpty()) {
            return;
        }

        for (ItemNotaEntrada item : nota.getItens()) {
            if (item.getProdutoId() != null) {
                Produto produto = produtoDAO.buscarPorId(item.getProdutoId());

                if (produto != null) {
                    // Reverte estoque (subtrai a quantidade)
                    Integer quantidadeAtual = produto.getQuantidadeEstoque() != null ? produto.getQuantidadeEstoque() : 0;
                    Integer quantidadeRemover = item.getQuantidade() != null ? item.getQuantidade().intValue() : 0;
                    Integer novaQuantidade = Math.max(0, quantidadeAtual - quantidadeRemover); // Não deixa ficar negativo
                    produtoDAO.atualizarEstoque(produto.getId(), novaQuantidade);

                    // Busca o valor unitário da nota mais recente deste produto (excluindo a nota cancelada)
                    BigDecimal valorAnterior = notaEntradaDAO.buscarValorUnitarioMaisRecenteProduto(item.getProdutoId(), nota.getId());
                    produtoDAO.atualizarValorCompra(produto.getId(), valorAnterior); // Se null, define como null

                    // Recalcula o custoProduto baseado na nota anterior
                    BigDecimal custoAnterior = calcularCustoProdutoNotaAnterior(item.getProdutoId(), nota.getId());
                    produtoDAO.atualizarCustoProduto(produto.getId(), custoAnterior);
                }
            }
        }
    }

    /**
     * Calcula o custo do produto com rateio de despesas da nota
     * Fórmula: custoProduto = valorUnitario + (despesasRateadas / quantidade)
     */
    private BigDecimal calcularCustoProdutoComRateio(NotaEntrada nota, ItemNotaEntrada item) {
        if (item.getValorUnitario() == null || item.getQuantidade() == null) {
            return null;
        }

        // Calcula as despesas totais da nota (frete + seguro + outras - desconto)
        BigDecimal frete = nota.getValorFrete() != null ? nota.getValorFrete() : BigDecimal.ZERO;
        BigDecimal seguro = nota.getValorSeguro() != null ? nota.getValorSeguro() : BigDecimal.ZERO;
        BigDecimal outrasDespesas = nota.getOutrasDespesas() != null ? nota.getOutrasDespesas() : BigDecimal.ZERO;
        BigDecimal desconto = nota.getValorDesconto() != null ? nota.getValorDesconto() : BigDecimal.ZERO;

        BigDecimal despesasTotais = frete.add(seguro).add(outrasDespesas).subtract(desconto);

        // Se não há despesas, o custo é igual ao valor de compra
        if (despesasTotais.compareTo(BigDecimal.ZERO) <= 0) {
            return item.getValorUnitario();
        }

        // Calcula o valor total dos produtos na nota
        BigDecimal valorProdutos = nota.getValorProdutos() != null ? nota.getValorProdutos() : BigDecimal.ZERO;

        // Se valorProdutos é zero, retorna apenas o valor unitário para evitar divisão por zero
        if (valorProdutos.compareTo(BigDecimal.ZERO) == 0) {
            return item.getValorUnitario();
        }

        // Calcula o valor total deste item (valorUnitario * quantidade)
        BigDecimal valorTotalItem = item.getValorUnitario().multiply(item.getQuantidade());

        // Calcula o percentual que este item representa do total de produtos
        BigDecimal percentualItem = valorTotalItem.divide(valorProdutos, 6, RoundingMode.HALF_UP);

        // Calcula as despesas rateadas para este item
        BigDecimal despesasRateadas = despesasTotais.multiply(percentualItem);

        // Calcula as despesas por unidade
        BigDecimal despesasPorUnidade = despesasRateadas.divide(item.getQuantidade(), 6, RoundingMode.HALF_UP);

        // Custo final = valor unitário + despesas por unidade
        return item.getValorUnitario().add(despesasPorUnidade).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Busca e calcula o custo do produto baseado na nota anterior (mais recente antes da cancelada)
     * Retorna null se não houver nota anterior
     */
    private BigDecimal calcularCustoProdutoNotaAnterior(Long produtoId, Long notaCanceladaId) {
        // Busca a nota anterior mais recente deste produto
        NotaEntrada notaAnterior = notaEntradaDAO.buscarNotaMaisRecenteProduto(produtoId, notaCanceladaId);

        if (notaAnterior == null) {
            return null; // Não há nota anterior, custo fica null
        }

        // Busca o item do produto na nota anterior
        ItemNotaEntrada itemAnterior = notaAnterior.getItens().stream()
                .filter(item -> produtoId.equals(item.getProdutoId()))
                .findFirst()
                .orElse(null);

        if (itemAnterior == null) {
            return null;
        }

        // Calcula o custo com base na nota anterior
        return calcularCustoProdutoComRateio(notaAnterior, itemAnterior);
    }
}
