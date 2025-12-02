package com.cantina.database;

import com.cantina.entities.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class NotaSaidaDAO {

    public void salvar(NotaSaida notaSaida) {
        String sql = "INSERT INTO notas_saida (numero, codigo, modelo, serie, cliente_id, " +
                "data_emissao, data_chegada, data_recebimento, condicao_pagamento_id, status, " +
                "tipo_frete, transportadora_id, valor_frete, valor_seguro, outras_despesas, " +
                "valor_desconto, valor_produtos, valor_total, observacoes, ativo, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (notaSaida.getItens() != null && !notaSaida.getItens().isEmpty()) {
                for (ItemNotaSaida item : notaSaida.getItens()) {
                    calcularValorTotalItem(item);
                }
            }

            calcularValores(notaSaida);

            statement.setString(1, notaSaida.getNumero());
            statement.setString(2, notaSaida.getCodigo());
            statement.setString(3, notaSaida.getModelo());
            statement.setString(4, notaSaida.getSerie());
            statement.setLong(5, notaSaida.getClienteId() != null ? notaSaida.getClienteId() :
                    (notaSaida.getCliente() != null ? notaSaida.getCliente().getId() : null));

            statement.setDate(6, notaSaida.getDataEmissao() != null ? new java.sql.Date(notaSaida.getDataEmissao().getTime()) : null);
            statement.setDate(7, notaSaida.getDataChegada() != null ? new java.sql.Date(notaSaida.getDataChegada().getTime()) : null);
            statement.setDate(8, notaSaida.getDataRecebimento() != null ? new java.sql.Date(notaSaida.getDataRecebimento().getTime()) : null);

            statement.setLong(9, notaSaida.getCondicaoPagamentoId() != null ? notaSaida.getCondicaoPagamentoId() :
                    (notaSaida.getCondicaoPagamento() != null ? notaSaida.getCondicaoPagamento().getId() : null));
            statement.setString(10, notaSaida.getStatus() != null ? notaSaida.getStatus() : "PENDENTE");
            statement.setString(11, notaSaida.getTipoFrete() != null ? notaSaida.getTipoFrete() : "CIF");
            statement.setObject(12, notaSaida.getTransportadoraId() != null ? notaSaida.getTransportadoraId() :
                    (notaSaida.getTransportadora() != null ? notaSaida.getTransportadora().getId() : null));

            statement.setBigDecimal(13, notaSaida.getValorFrete() != null ? notaSaida.getValorFrete() : BigDecimal.ZERO);
            statement.setBigDecimal(14, notaSaida.getValorSeguro() != null ? notaSaida.getValorSeguro() : BigDecimal.ZERO);
            statement.setBigDecimal(15, notaSaida.getOutrasDespesas() != null ? notaSaida.getOutrasDespesas() : BigDecimal.ZERO);
            statement.setBigDecimal(16, notaSaida.getValorDesconto() != null ? notaSaida.getValorDesconto() : BigDecimal.ZERO);

            statement.setBigDecimal(17, notaSaida.getValorProdutos());
            statement.setBigDecimal(18, notaSaida.getValorTotal());

            statement.setString(19, notaSaida.getObservacoes());
            statement.setBoolean(20, notaSaida.getAtivo() != null ? notaSaida.getAtivo() : true);

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                notaSaida.setId(generatedKeys.getLong(1));
            }

            if (notaSaida.getItens() != null && !notaSaida.getItens().isEmpty()) {
                ItemNotaSaidaDAO itemDAO = new ItemNotaSaidaDAO();
                itemDAO.salvarLista(notaSaida.getId(), notaSaida.getItens());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar nota de saída: " + e.getMessage(), e);
        }
    }


    public List<NotaSaida> listarTodas() {
        List<NotaSaida> notasSaida = new ArrayList<>();
        String sql = "SELECT * FROM notas_saida WHERE ativo = true ORDER BY data_emissao DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                notasSaida.add(mapearNotaSaida(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notasSaida;
    }

    public NotaSaida buscarPorId(Long id) {
        String sql = "SELECT * FROM notas_saida WHERE id = ?";
        NotaSaida notaSaida = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                notaSaida = mapearNotaSaida(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notaSaida;
    }

    public NotaSaida buscarPorNumero(String numero) {
        String sql = "SELECT * FROM notas_saida WHERE numero = ?";
        NotaSaida notaSaida = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, numero);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                notaSaida = mapearNotaSaida(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notaSaida;
    }

    public void atualizar(NotaSaida notaSaida) {
        calcularValores(notaSaida);

        String sql = "UPDATE notas_saida SET numero = ?, codigo = ?, modelo = ?, serie = ?, " +
                "cliente_id = ?, data_emissao = ?, data_chegada = ?, data_recebimento = ?, " +
                "condicao_pagamento_id = ?, status = ?, tipo_frete = ?, transportadora_id = ?, " +
                "valor_frete = ?, valor_seguro = ?, outras_despesas = ?, valor_desconto = ?, " +
                "valor_produtos = ?, valor_total = ?, observacoes = ?, ativo = ?, updated_at = NOW() " +
                "WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, notaSaida.getNumero());
            statement.setString(2, notaSaida.getCodigo());
            statement.setString(3, notaSaida.getModelo());
            statement.setString(4, notaSaida.getSerie());
            statement.setLong(5, notaSaida.getClienteId() != null ? notaSaida.getClienteId() :
                    (notaSaida.getCliente() != null ? notaSaida.getCliente().getId() : null));

            statement.setDate(6, notaSaida.getDataEmissao() != null ? new java.sql.Date(notaSaida.getDataEmissao().getTime()) : null);
            statement.setDate(7, notaSaida.getDataChegada() != null ? new java.sql.Date(notaSaida.getDataChegada().getTime()) : null);
            statement.setDate(8, notaSaida.getDataRecebimento() != null ? new java.sql.Date(notaSaida.getDataRecebimento().getTime()) : null);

            statement.setLong(9, notaSaida.getCondicaoPagamentoId() != null ? notaSaida.getCondicaoPagamentoId() :
                    (notaSaida.getCondicaoPagamento() != null ? notaSaida.getCondicaoPagamento().getId() : null));
            statement.setString(10, notaSaida.getStatus() != null ? notaSaida.getStatus() : "PENDENTE");
            statement.setString(11, notaSaida.getTipoFrete() != null ? notaSaida.getTipoFrete() : "CIF");
            statement.setObject(12, notaSaida.getTransportadoraId() != null ? notaSaida.getTransportadoraId() :
                    (notaSaida.getTransportadora() != null ? notaSaida.getTransportadora().getId() : null));

            statement.setBigDecimal(13, notaSaida.getValorFrete() != null ? notaSaida.getValorFrete() : BigDecimal.ZERO);
            statement.setBigDecimal(14, notaSaida.getValorSeguro() != null ? notaSaida.getValorSeguro() : BigDecimal.ZERO);
            statement.setBigDecimal(15, notaSaida.getOutrasDespesas() != null ? notaSaida.getOutrasDespesas() : BigDecimal.ZERO);
            statement.setBigDecimal(16, notaSaida.getValorDesconto() != null ? notaSaida.getValorDesconto() : BigDecimal.ZERO);
            statement.setBigDecimal(17, notaSaida.getValorProdutos());
            statement.setBigDecimal(18, notaSaida.getValorTotal());
            statement.setString(19, notaSaida.getObservacoes());
            statement.setBoolean(20, notaSaida.getAtivo() != null ? notaSaida.getAtivo() : true);
            statement.setLong(21, notaSaida.getId());

            statement.executeUpdate();

            if (notaSaida.getItens() != null) {
                ItemNotaSaidaDAO itemDAO = new ItemNotaSaidaDAO();
                itemDAO.excluirPorNotaSaida(notaSaida.getId());
                itemDAO.salvarLista(notaSaida.getId(), notaSaida.getItens());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar nota de saída: " + e.getMessage(), e);
        }
    }

    public void excluir(Long id) {
        String sql = "UPDATE notas_saida SET ativo = false, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void atualizarStatus(Long id, String novoStatus) {
        String sql = "UPDATE notas_saida SET status = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, novoStatus);
            statement.setLong(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private NotaSaida mapearNotaSaida(ResultSet resultSet) throws SQLException {
        NotaSaida notaSaida = new NotaSaida();
        notaSaida.setId(resultSet.getLong("id"));
        notaSaida.setNumero(resultSet.getString("numero"));
        notaSaida.setCodigo(resultSet.getString("codigo"));
        notaSaida.setModelo(resultSet.getString("modelo"));
        notaSaida.setSerie(resultSet.getString("serie"));
        notaSaida.setClienteId(resultSet.getLong("cliente_id"));
        notaSaida.setDataEmissao(resultSet.getDate("data_emissao"));
        notaSaida.setDataChegada(resultSet.getDate("data_chegada"));
        notaSaida.setDataRecebimento(resultSet.getDate("data_recebimento"));
        notaSaida.setCondicaoPagamentoId(resultSet.getLong("condicao_pagamento_id"));
        notaSaida.setStatus(resultSet.getString("status"));
        notaSaida.setTipoFrete(resultSet.getString("tipo_frete"));
        notaSaida.setTransportadoraId((Long) resultSet.getObject("transportadora_id"));
        notaSaida.setValorFrete(resultSet.getBigDecimal("valor_frete"));
        notaSaida.setValorSeguro(resultSet.getBigDecimal("valor_seguro"));
        notaSaida.setOutrasDespesas(resultSet.getBigDecimal("outras_despesas"));
        notaSaida.setValorDesconto(resultSet.getBigDecimal("valor_desconto"));
        notaSaida.setValorProdutos(resultSet.getBigDecimal("valor_produtos"));
        notaSaida.setValorTotal(resultSet.getBigDecimal("valor_total"));
        notaSaida.setObservacoes(resultSet.getString("observacoes"));
        notaSaida.setAtivo(resultSet.getBoolean("ativo"));
        notaSaida.setDataCriacao(resultSet.getDate("created_at"));
        notaSaida.setUltimaModificacao(resultSet.getDate("updated_at"));

        ItemNotaSaidaDAO itemDAO = new ItemNotaSaidaDAO();
        notaSaida.setItens(itemDAO.buscarPorNotaSaida(notaSaida.getId()));

        if (notaSaida.getClienteId() != null) {
            ClienteDAO clienteDAO = new ClienteDAO();
            notaSaida.setCliente(clienteDAO.buscarPorId(notaSaida.getClienteId()));
        }

        if (notaSaida.getCondicaoPagamentoId() != null) {
            CondicaoPagamentoDAO condicaoPagamentoDAO = new CondicaoPagamentoDAO();
            notaSaida.setCondicaoPagamento(condicaoPagamentoDAO.buscarPorId(notaSaida.getCondicaoPagamentoId()));
        }

        if (notaSaida.getTransportadoraId() != null) {
            TransportadoraDAO transportadoraDAO = new TransportadoraDAO();
            notaSaida.setTransportadora(transportadoraDAO.buscarPorId(notaSaida.getTransportadoraId()));
        }

        return notaSaida;
    }



    private void calcularValores(NotaSaida notaSaida) {
        BigDecimal valorProdutos = BigDecimal.ZERO;
        BigDecimal valorDescontoItens = BigDecimal.ZERO;

        if (notaSaida.getItens() != null && !notaSaida.getItens().isEmpty()) {
            for (ItemNotaSaida item : notaSaida.getItens()) {
                BigDecimal quantidade = item.getQuantidade() != null ? item.getQuantidade() : BigDecimal.ZERO;
                BigDecimal valorUnitario = item.getValorUnitario() != null ? item.getValorUnitario() : BigDecimal.ZERO;
                BigDecimal valorBrutoItem = quantidade.multiply(valorUnitario);

                valorProdutos = valorProdutos.add(valorBrutoItem);

                BigDecimal descontoItem = item.getValorDesconto() != null ? item.getValorDesconto() : BigDecimal.ZERO;
                valorDescontoItens = valorDescontoItens.add(descontoItem);
            }
        }

        notaSaida.setValorProdutos(valorProdutos);

        BigDecimal descontoNota = notaSaida.getValorDesconto() != null ? notaSaida.getValorDesconto() : BigDecimal.ZERO;
        BigDecimal valorDescontoTotal = valorDescontoItens.add(descontoNota);

        notaSaida.setValorDesconto(valorDescontoTotal);

        BigDecimal valorTotal = valorProdutos;

        valorTotal = valorTotal.subtract(valorDescontoTotal);

        if (notaSaida.getValorFrete() != null) {
            valorTotal = valorTotal.add(notaSaida.getValorFrete());
        }
        if (notaSaida.getValorSeguro() != null) {
            valorTotal = valorTotal.add(notaSaida.getValorSeguro());
        }
        if (notaSaida.getOutrasDespesas() != null) {
            valorTotal = valorTotal.add(notaSaida.getOutrasDespesas());
        }

        notaSaida.setValorTotal(valorTotal);
    }


    private void calcularValorTotalItem(ItemNotaSaida item) {
        if (item.getValorTotal() == null || item.getValorTotal().compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal quantidade = item.getQuantidade() != null ? item.getQuantidade() : BigDecimal.ZERO;
            BigDecimal valorUnitario = item.getValorUnitario() != null ? item.getValorUnitario() : BigDecimal.ZERO;
            BigDecimal valorDesconto = item.getValorDesconto() != null ? item.getValorDesconto() : BigDecimal.ZERO;

            BigDecimal valorTotal = quantidade.multiply(valorUnitario).subtract(valorDesconto);
            item.setValorTotal(valorTotal);
        }
    }

}
