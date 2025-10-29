package com.cantina.database;

import com.cantina.entities.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class NotaEntradaDAO {

    public void salvar(NotaEntrada notaEntrada) {
        String sql = "INSERT INTO notas_entrada (numero, codigo, modelo, serie, fornecedor_id, " +
                "data_emissao, data_chegada, data_recebimento, condicao_pagamento_id, status, " +
                "tipo_frete, transportadora_id, valor_frete, valor_seguro, outras_despesas, " +
                "valor_desconto, valor_produtos, valor_total, observacoes, ativo, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (notaEntrada.getItens() != null && !notaEntrada.getItens().isEmpty()) {
                for (ItemNotaEntrada item : notaEntrada.getItens()) {
                    calcularValorTotalItem(item);
                }
            }

            calcularValores(notaEntrada);

            statement.setString(1, notaEntrada.getNumero());
            statement.setString(2, notaEntrada.getCodigo());
            statement.setString(3, notaEntrada.getModelo());
            statement.setString(4, notaEntrada.getSerie());
            statement.setLong(5, notaEntrada.getFornecedorId() != null ? notaEntrada.getFornecedorId() :
                    (notaEntrada.getFornecedor() != null ? notaEntrada.getFornecedor().getId() : null));

            statement.setDate(6, notaEntrada.getDataEmissao() != null ? new java.sql.Date(notaEntrada.getDataEmissao().getTime()) : null);
            statement.setDate(7, notaEntrada.getDataChegada() != null ? new java.sql.Date(notaEntrada.getDataChegada().getTime()) : null);
            statement.setDate(8, notaEntrada.getDataRecebimento() != null ? new java.sql.Date(notaEntrada.getDataRecebimento().getTime()) : null);

            statement.setLong(9, notaEntrada.getCondicaoPagamentoId() != null ? notaEntrada.getCondicaoPagamentoId() :
                    (notaEntrada.getCondicaoPagamento() != null ? notaEntrada.getCondicaoPagamento().getId() : null));
            statement.setString(10, notaEntrada.getStatus() != null ? notaEntrada.getStatus() : "PENDENTE");
            statement.setString(11, notaEntrada.getTipoFrete() != null ? notaEntrada.getTipoFrete() : "CIF");
            statement.setObject(12, notaEntrada.getTransportadoraId() != null ? notaEntrada.getTransportadoraId() :
                    (notaEntrada.getTransportadora() != null ? notaEntrada.getTransportadora().getId() : null));

            statement.setBigDecimal(13, notaEntrada.getValorFrete() != null ? notaEntrada.getValorFrete() : BigDecimal.ZERO);
            statement.setBigDecimal(14, notaEntrada.getValorSeguro() != null ? notaEntrada.getValorSeguro() : BigDecimal.ZERO);
            statement.setBigDecimal(15, notaEntrada.getOutrasDespesas() != null ? notaEntrada.getOutrasDespesas() : BigDecimal.ZERO);
            statement.setBigDecimal(16, notaEntrada.getValorDesconto() != null ? notaEntrada.getValorDesconto() : BigDecimal.ZERO);

            statement.setBigDecimal(17, notaEntrada.getValorProdutos());
            statement.setBigDecimal(18, notaEntrada.getValorTotal());

            statement.setString(19, notaEntrada.getObservacoes());
            statement.setBoolean(20, notaEntrada.getAtivo() != null ? notaEntrada.getAtivo() : true);

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                notaEntrada.setId(generatedKeys.getLong(1));
            }

            if (notaEntrada.getItens() != null && !notaEntrada.getItens().isEmpty()) {
                ItemNotaEntradaDAO itemDAO = new ItemNotaEntradaDAO();
                itemDAO.salvarLista(notaEntrada.getId(), notaEntrada.getItens());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar nota de entrada: " + e.getMessage(), e);
        }
    }


    public List<NotaEntrada> listarTodas() {
        List<NotaEntrada> notasEntrada = new ArrayList<>();
        String sql = "SELECT * FROM notas_entrada WHERE ativo = true ORDER BY data_emissao DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                notasEntrada.add(mapearNotaEntrada(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notasEntrada;
    }

    public NotaEntrada buscarPorId(Long id) {
        String sql = "SELECT * FROM notas_entrada WHERE id = ?";
        NotaEntrada notaEntrada = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                notaEntrada = mapearNotaEntrada(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notaEntrada;
    }

    public NotaEntrada buscarPorNumero(String numero) {
        String sql = "SELECT * FROM notas_entrada WHERE numero = ?";
        NotaEntrada notaEntrada = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, numero);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                notaEntrada = mapearNotaEntrada(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notaEntrada;
    }

    public void atualizar(NotaEntrada notaEntrada) {
        calcularValores(notaEntrada);

        String sql = "UPDATE notas_entrada SET numero = ?, codigo = ?, modelo = ?, serie = ?, " +
                "fornecedor_id = ?, data_emissao = ?, data_chegada = ?, data_recebimento = ?, " +
                "condicao_pagamento_id = ?, status = ?, tipo_frete = ?, transportadora_id = ?, " +
                "valor_frete = ?, valor_seguro = ?, outras_despesas = ?, valor_desconto = ?, " +
                "valor_produtos = ?, valor_total = ?, observacoes = ?, ativo = ?, updated_at = NOW() " +
                "WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, notaEntrada.getNumero());
            statement.setString(2, notaEntrada.getCodigo());
            statement.setString(3, notaEntrada.getModelo());
            statement.setString(4, notaEntrada.getSerie());
            statement.setLong(5, notaEntrada.getFornecedorId() != null ? notaEntrada.getFornecedorId() :
                    (notaEntrada.getFornecedor() != null ? notaEntrada.getFornecedor().getId() : null));

            statement.setDate(6, notaEntrada.getDataEmissao() != null ? new java.sql.Date(notaEntrada.getDataEmissao().getTime()) : null);
            statement.setDate(7, notaEntrada.getDataChegada() != null ? new java.sql.Date(notaEntrada.getDataChegada().getTime()) : null);
            statement.setDate(8, notaEntrada.getDataRecebimento() != null ? new java.sql.Date(notaEntrada.getDataRecebimento().getTime()) : null);

            statement.setLong(9, notaEntrada.getCondicaoPagamentoId() != null ? notaEntrada.getCondicaoPagamentoId() :
                    (notaEntrada.getCondicaoPagamento() != null ? notaEntrada.getCondicaoPagamento().getId() : null));
            statement.setString(10, notaEntrada.getStatus() != null ? notaEntrada.getStatus() : "PENDENTE");
            statement.setString(11, notaEntrada.getTipoFrete() != null ? notaEntrada.getTipoFrete() : "CIF");
            statement.setObject(12, notaEntrada.getTransportadoraId() != null ? notaEntrada.getTransportadoraId() :
                    (notaEntrada.getTransportadora() != null ? notaEntrada.getTransportadora().getId() : null));

            statement.setBigDecimal(13, notaEntrada.getValorFrete() != null ? notaEntrada.getValorFrete() : BigDecimal.ZERO);
            statement.setBigDecimal(14, notaEntrada.getValorSeguro() != null ? notaEntrada.getValorSeguro() : BigDecimal.ZERO);
            statement.setBigDecimal(15, notaEntrada.getOutrasDespesas() != null ? notaEntrada.getOutrasDespesas() : BigDecimal.ZERO);
            statement.setBigDecimal(16, notaEntrada.getValorDesconto() != null ? notaEntrada.getValorDesconto() : BigDecimal.ZERO);
            statement.setBigDecimal(17, notaEntrada.getValorProdutos());
            statement.setBigDecimal(18, notaEntrada.getValorTotal());
            statement.setString(19, notaEntrada.getObservacoes());
            statement.setBoolean(20, notaEntrada.getAtivo() != null ? notaEntrada.getAtivo() : true);
            statement.setLong(21, notaEntrada.getId());

            statement.executeUpdate();

            if (notaEntrada.getItens() != null) {
                ItemNotaEntradaDAO itemDAO = new ItemNotaEntradaDAO();
                itemDAO.excluirPorNotaEntrada(notaEntrada.getId());
                itemDAO.salvarLista(notaEntrada.getId(), notaEntrada.getItens());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar nota de entrada: " + e.getMessage(), e);
        }
    }

    public void excluir(Long id) {
        String sql = "UPDATE notas_entrada SET ativo = false, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void atualizarStatus(Long id, String novoStatus) {
        String sql = "UPDATE notas_entrada SET status = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, novoStatus);
            statement.setLong(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private NotaEntrada mapearNotaEntrada(ResultSet resultSet) throws SQLException {
        NotaEntrada notaEntrada = new NotaEntrada();
        notaEntrada.setId(resultSet.getLong("id"));
        notaEntrada.setNumero(resultSet.getString("numero"));
        notaEntrada.setCodigo(resultSet.getString("codigo"));
        notaEntrada.setModelo(resultSet.getString("modelo"));
        notaEntrada.setSerie(resultSet.getString("serie"));
        notaEntrada.setFornecedorId(resultSet.getLong("fornecedor_id"));
        notaEntrada.setDataEmissao(resultSet.getDate("data_emissao"));
        notaEntrada.setDataChegada(resultSet.getDate("data_chegada"));
        notaEntrada.setDataRecebimento(resultSet.getDate("data_recebimento"));
        notaEntrada.setCondicaoPagamentoId(resultSet.getLong("condicao_pagamento_id"));
        notaEntrada.setStatus(resultSet.getString("status"));
        notaEntrada.setTipoFrete(resultSet.getString("tipo_frete"));
        notaEntrada.setTransportadoraId((Long) resultSet.getObject("transportadora_id"));
        notaEntrada.setValorFrete(resultSet.getBigDecimal("valor_frete"));
        notaEntrada.setValorSeguro(resultSet.getBigDecimal("valor_seguro"));
        notaEntrada.setOutrasDespesas(resultSet.getBigDecimal("outras_despesas"));
        notaEntrada.setValorDesconto(resultSet.getBigDecimal("valor_desconto"));
        notaEntrada.setValorProdutos(resultSet.getBigDecimal("valor_produtos"));
        notaEntrada.setValorTotal(resultSet.getBigDecimal("valor_total"));
        notaEntrada.setObservacoes(resultSet.getString("observacoes"));
        notaEntrada.setAtivo(resultSet.getBoolean("ativo"));
        notaEntrada.setDataCriacao(resultSet.getDate("created_at"));
        notaEntrada.setUltimaModificacao(resultSet.getDate("updated_at"));

        ItemNotaEntradaDAO itemDAO = new ItemNotaEntradaDAO();
        notaEntrada.setItens(itemDAO.buscarPorNotaEntrada(notaEntrada.getId()));

        if (notaEntrada.getFornecedorId() != null) {
            FornecedorDAO fornecedorDAO = new FornecedorDAO();
            notaEntrada.setFornecedor(fornecedorDAO.buscarPorId(notaEntrada.getFornecedorId()));
        }

        if (notaEntrada.getCondicaoPagamentoId() != null) {
            CondicaoPagamentoDAO condicaoPagamentoDAO = new CondicaoPagamentoDAO();
            notaEntrada.setCondicaoPagamento(condicaoPagamentoDAO.buscarPorId(notaEntrada.getCondicaoPagamentoId()));
        }

        if (notaEntrada.getTransportadoraId() != null) {
            TransportadoraDAO transportadoraDAO = new TransportadoraDAO();
            notaEntrada.setTransportadora(transportadoraDAO.buscarPorId(notaEntrada.getTransportadoraId()));
        }

        ItemNotaEntradaDAO itemNotaEntradaDAO = new ItemNotaEntradaDAO();
        notaEntrada.setItens(itemDAO.buscarPorNotaEntrada(notaEntrada.getId()));

        return notaEntrada;
    }



    private void calcularValores(NotaEntrada notaEntrada) {
        BigDecimal valorProdutos = BigDecimal.ZERO;
        BigDecimal valorDescontoItens = BigDecimal.ZERO;

        if (notaEntrada.getItens() != null && !notaEntrada.getItens().isEmpty()) {
            for (ItemNotaEntrada item : notaEntrada.getItens()) {
                BigDecimal quantidade = item.getQuantidade() != null ? item.getQuantidade() : BigDecimal.ZERO;
                BigDecimal valorUnitario = item.getValorUnitario() != null ? item.getValorUnitario() : BigDecimal.ZERO;
                BigDecimal valorBrutoItem = quantidade.multiply(valorUnitario);

                valorProdutos = valorProdutos.add(valorBrutoItem);

                BigDecimal descontoItem = item.getValorDesconto() != null ? item.getValorDesconto() : BigDecimal.ZERO;
                valorDescontoItens = valorDescontoItens.add(descontoItem);
            }
        }

        notaEntrada.setValorProdutos(valorProdutos);

        // Considera desconto dos itens + desconto geral da nota (se houver)
        BigDecimal descontoNota = notaEntrada.getValorDesconto() != null ? notaEntrada.getValorDesconto() : BigDecimal.ZERO;
        BigDecimal valorDescontoTotal = valorDescontoItens.add(descontoNota);

        // Atualiza o desconto total na nota (itens + nota)
        notaEntrada.setValorDesconto(valorDescontoTotal);

        BigDecimal valorTotal = valorProdutos;

        valorTotal = valorTotal.subtract(valorDescontoTotal);

        if (notaEntrada.getValorFrete() != null) {
            valorTotal = valorTotal.add(notaEntrada.getValorFrete());
        }
        if (notaEntrada.getValorSeguro() != null) {
            valorTotal = valorTotal.add(notaEntrada.getValorSeguro());
        }
        if (notaEntrada.getOutrasDespesas() != null) {
            valorTotal = valorTotal.add(notaEntrada.getOutrasDespesas());
        }

        notaEntrada.setValorTotal(valorTotal);
    }


    private void calcularValorTotalItem(ItemNotaEntrada item) {
        if (item.getValorTotal() == null || item.getValorTotal().compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal quantidade = item.getQuantidade() != null ? item.getQuantidade() : BigDecimal.ZERO;
            BigDecimal valorUnitario = item.getValorUnitario() != null ? item.getValorUnitario() : BigDecimal.ZERO;
            BigDecimal valorDesconto = item.getValorDesconto() != null ? item.getValorDesconto() : BigDecimal.ZERO;

            BigDecimal valorTotal = quantidade.multiply(valorUnitario).subtract(valorDesconto);
            item.setValorTotal(valorTotal);
        }
    }

}
