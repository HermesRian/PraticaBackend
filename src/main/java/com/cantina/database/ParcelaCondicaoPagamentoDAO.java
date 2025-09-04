package com.cantina.database;

import com.cantina.entities.ParcelaCondicaoPagamento;
import com.cantina.entities.CondicaoPagamento;
import com.cantina.entities.FormaPagamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParcelaCondicaoPagamentoDAO {

    public void salvar(ParcelaCondicaoPagamento parcela) {
        String sql = "INSERT INTO parcela_condicao_pagamento (numero_parcela, dias, percentual, condicao_pagamento_id, forma_pagamento_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, parcela.getNumeroParcela());
            statement.setInt(2, parcela.getDias());
            statement.setDouble(3, parcela.getPercentual());
            statement.setLong(4, parcela.getCondicaoPagamento().getId());
            statement.setLong(5, parcela.getFormaPagamento().getId());

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
        String sql = "SELECT * FROM parcela_condicao_pagamento";

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
        String sql = "SELECT * FROM parcela_condicao_pagamento WHERE id = ?";
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
        String sql = "UPDATE parcela_condicao_pagamento SET numero_parcela = ?, dias = ?, percentual = ?, condicao_pagamento_id = ?, forma_pagamento_id = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, parcela.getNumeroParcela());
            statement.setInt(2, parcela.getDias());
            statement.setDouble(3, parcela.getPercentual());
            statement.setLong(4, parcela.getCondicaoPagamento().getId());
            statement.setLong(5, parcela.getFormaPagamento().getId());
            statement.setLong(6, parcela.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM parcela_condicao_pagamento WHERE id = ?";

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

        CondicaoPagamento condicaoPagamento = new CondicaoPagamento();
        condicaoPagamento.setId(resultSet.getLong("condicao_pagamento_id"));
        parcela.setCondicaoPagamento(condicaoPagamento);

        FormaPagamento formaPagamento = buscarFormaPagamentoPorId(resultSet.getLong("forma_pagamento_id"));
        parcela.setFormaPagamento(formaPagamento);

        return parcela;
    }

    public List<ParcelaCondicaoPagamento> buscarPorCondicaoPagamentoId(Long condicaoPagamentoId) {
        List<ParcelaCondicaoPagamento> parcelas = new ArrayList<>();
        String sql = "SELECT * FROM parcela_condicao_pagamento WHERE condicao_pagamento_id = ?";

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
        String sql = "SELECT * FROM forma_pagamento WHERE id = ?";
        FormaPagamento formaPagamento = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                formaPagamento = new FormaPagamento();
                formaPagamento.setId(resultSet.getLong("id"));
                formaPagamento.setAtivo(resultSet.getBoolean("status"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return formaPagamento;
    }
}