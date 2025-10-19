package com.cantina.database;

import com.cantina.entities.FormaPagamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FormaPagamentoDAO {

    public void salvar(FormaPagamento formaPagamento) {
        String sql = "INSERT INTO formas_pagamento (nome, status, created_at, updated_at) VALUES (?, ?, NOW(), NOW())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, formaPagamento.getNome());
            statement.setBoolean(2, formaPagamento.getAtivo() != null ? formaPagamento.getAtivo() : true);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<FormaPagamento> listarTodos() {
        List<FormaPagamento> formas = new ArrayList<>();
        String sql = "SELECT * FROM formas_pagamento";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                FormaPagamento forma = new FormaPagamento();
                forma.setId(resultSet.getLong("id"));
                forma.setNome(resultSet.getString("nome"));
                forma.setAtivo(resultSet.getBoolean("status"));
                forma.setDataCriacao(resultSet.getDate("created_at"));
                forma.setUltimaModificacao(resultSet.getDate("updated_at"));

                formas.add(forma);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return formas;
    }

    public FormaPagamento buscarPorId(Long id) {
        String sql = "SELECT * FROM formas_pagamento WHERE id = ?";
        FormaPagamento forma = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                forma = new FormaPagamento();
                forma.setId(resultSet.getLong("id"));
                forma.setNome(resultSet.getString("nome"));
                forma.setAtivo(resultSet.getBoolean("status"));
                forma.setDataCriacao(resultSet.getDate("created_at"));
                forma.setUltimaModificacao(resultSet.getDate("updated_at"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return forma;
    }

    public void atualizar(FormaPagamento formaPagamento) {
        String sql = "UPDATE formas_pagamento SET nome = ?, status = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, formaPagamento.getNome());
            statement.setBoolean(2, formaPagamento.getAtivo() != null ? formaPagamento.getAtivo() : true);
            statement.setLong(3, formaPagamento.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM formas_pagamento WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}