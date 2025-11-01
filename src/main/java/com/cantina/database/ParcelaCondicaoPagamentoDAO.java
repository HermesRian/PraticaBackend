package com.cantina.database;

import com.cantina.entities.ParcelaCondicaoPagamento;
import com.cantina.entities.CondicaoPagamento;
import com.cantina.entities.FormaPagamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParcelaCondicaoPagamentoDAO {

    public void salvar(ParcelaCondicaoPagamento parcela) {
        String sql = "INSERT INTO parcelas_condicao_pagamento (numero_parcela, dias, percentual, condicao_pagamento_id, forma_pagamento_id, data_vencimento, situacao, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, parcela.getNumeroParcela());
            statement.setInt(2, parcela.getDias());
            statement.setDouble(3, parcela.getPercentual());
            statement.setLong(4, parcela.getCondicaoPagamentoId() != null ? parcela.getCondicaoPagamentoId() : parcela.getCondicaoPagamento().getId());
            statement.setLong(5, parcela.getFormaPagamentoId() != null ? parcela.getFormaPagamentoId() : parcela.getFormaPagamento().getId());

            if (parcela.getDataVencimento() != null) {
                statement.setDate(6, new java.sql.Date(parcela.getDataVencimento().getTime()));
            } else {
                statement.setNull(6, Types.DATE);
            }

            statement.setString(7, parcela.getSituacao() != null ? parcela.getSituacao() : "A");

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                parcela.setId(generatedKeys.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<ParcelaCondicaoPagamento> listarTodos() {
        List<ParcelaCondicaoPagamento> parcelas = new ArrayList<>();
        String sql = "SELECT * FROM parcelas_condicao_pagamento";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ParcelaCondicaoPagamento parcela = mapearParcela(resultSet);
                parcelas.add(parcela);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parcelas;
    }

    public ParcelaCondicaoPagamento buscarPorId(Long id) {
        String sql = "SELECT * FROM parcelas_condicao_pagamento WHERE id = ?";
        ParcelaCondicaoPagamento parcela = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                parcela = mapearParcela(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parcela;
    }

    public void atualizar(ParcelaCondicaoPagamento parcela) {
        String sql = "UPDATE parcelas_condicao_pagamento SET numero_parcela = ?, dias = ?, percentual = ?, condicao_pagamento_id = ?, forma_pagamento_id = ?, data_vencimento = ?, situacao = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, parcela.getNumeroParcela());
            statement.setInt(2, parcela.getDias());
            statement.setDouble(3, parcela.getPercentual());
            statement.setLong(4, parcela.getCondicaoPagamentoId() != null ? parcela.getCondicaoPagamentoId() : parcela.getCondicaoPagamento().getId());
            statement.setLong(5, parcela.getFormaPagamentoId() != null ? parcela.getFormaPagamentoId() : parcela.getFormaPagamento().getId());

            if (parcela.getDataVencimento() != null) {
                statement.setDate(6, new java.sql.Date(parcela.getDataVencimento().getTime()));
            } else {
                statement.setNull(6, Types.DATE);
            }

            statement.setString(7, parcela.getSituacao() != null ? parcela.getSituacao() : "A");
            statement.setLong(8, parcela.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void excluir(Long id) {
        String sql = "DELETE FROM parcelas_condicao_pagamento WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ParcelaCondicaoPagamento mapearParcela(ResultSet resultSet) throws SQLException {
        ParcelaCondicaoPagamento parcela = new ParcelaCondicaoPagamento();
        parcela.setId(resultSet.getLong("id"));
        parcela.setNumeroParcela(resultSet.getInt("numero_parcela"));
        parcela.setDias(resultSet.getInt("dias"));
        parcela.setPercentual(resultSet.getDouble("percentual"));
        parcela.setCondicaoPagamentoId(resultSet.getLong("condicao_pagamento_id"));
        parcela.setFormaPagamentoId(resultSet.getLong("forma_pagamento_id"));
        parcela.setDataVencimento(resultSet.getDate("data_vencimento"));
        parcela.setSituacao(resultSet.getString("situacao"));
        parcela.setDataCriacao(resultSet.getDate("created_at"));
        parcela.setUltimaModificacao(resultSet.getDate("updated_at"));

        CondicaoPagamento condicaoPagamento = new CondicaoPagamento();
        condicaoPagamento.setId(resultSet.getLong("condicao_pagamento_id"));
        parcela.setCondicaoPagamento(condicaoPagamento);

        FormaPagamento formaPagamento = buscarFormaPagamentoPorId(resultSet.getLong("forma_pagamento_id"));
        parcela.setFormaPagamento(formaPagamento);

        return parcela;
    }


    public List<ParcelaCondicaoPagamento> buscarPorCondicaoPagamentoId(Long condicaoPagamentoId) {
        List<ParcelaCondicaoPagamento> parcelas = new ArrayList<>();
        String sql = "SELECT * FROM parcelas_condicao_pagamento WHERE condicao_pagamento_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, condicaoPagamentoId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ParcelaCondicaoPagamento parcela = mapearParcela(resultSet);
                parcelas.add(parcela);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parcelas;
    }

    private FormaPagamento buscarFormaPagamentoPorId(Long id) {
        String sql = "SELECT * FROM formas_pagamento WHERE id = ?";
        FormaPagamento formaPagamento = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                formaPagamento = new FormaPagamento();
                formaPagamento.setId(resultSet.getLong("id"));
                formaPagamento.setNome(resultSet.getString("nome"));
                formaPagamento.setAtivo(resultSet.getBoolean("status"));
                formaPagamento.setDataCriacao(resultSet.getDate("created_at"));
                formaPagamento.setUltimaModificacao(resultSet.getDate("updated_at"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return formaPagamento;
    }
}