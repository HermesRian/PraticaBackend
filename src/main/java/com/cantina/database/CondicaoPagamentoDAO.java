package com.cantina.database;

import com.cantina.entities.CondicaoPagamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.cantina.entities.ParcelaCondicaoPagamento;
import com.cantina.database.ParcelaCondicaoPagamentoDAO;


public class CondicaoPagamentoDAO {

    public void salvar(CondicaoPagamento condicaoPagamento) {
        String sql = "INSERT INTO condicoes_pagamento (nome, dias, parcelas, status, juros_percentual, multa_percentual, desconto_percentual, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, condicaoPagamento.getNome());
            statement.setInt(2, condicaoPagamento.getDias());
            statement.setInt(3, condicaoPagamento.getParcelas());
            statement.setBoolean(4, condicaoPagamento.getAtivo() != null ? condicaoPagamento.getAtivo() : true);
            statement.setDouble(5, condicaoPagamento.getJurosPercentual());
            statement.setDouble(6, condicaoPagamento.getMultaPercentual());
            statement.setDouble(7, condicaoPagamento.getDescontoPercentual());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                condicaoPagamento.setId(generatedKeys.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<CondicaoPagamento> listarTodos() {
        List<CondicaoPagamento> condicoes = new ArrayList<>();
        String sql = "SELECT * FROM condicoes_pagamento";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            ParcelaCondicaoPagamentoDAO parcelaDAO = new ParcelaCondicaoPagamentoDAO();

            while (resultSet.next()) {
                CondicaoPagamento condicao = new CondicaoPagamento();
                condicao.setId(resultSet.getLong("id"));
                condicao.setNome(resultSet.getString("nome"));
                condicao.setDias(resultSet.getInt("dias"));
                condicao.setParcelas(resultSet.getInt("parcelas"));
                condicao.setAtivo(resultSet.getBoolean("status"));
                condicao.setJurosPercentual(resultSet.getDouble("juros_percentual"));
                condicao.setMultaPercentual(resultSet.getDouble("multa_percentual"));
                condicao.setDescontoPercentual(resultSet.getDouble("desconto_percentual"));
                condicao.setDataCriacao(resultSet.getDate("created_at"));
                condicao.setUltimaModificacao(resultSet.getDate("updated_at"));

                condicao.setParcelasCondicao(parcelaDAO.buscarPorCondicaoPagamentoId(condicao.getId()));

                condicoes.add(condicao);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return condicoes;
    }

    public CondicaoPagamento buscarPorId(Long id) {
        String sql = "SELECT * FROM condicoes_pagamento WHERE id = ?";
        CondicaoPagamento condicao = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                condicao = new CondicaoPagamento();
                condicao.setId(resultSet.getLong("id"));
                condicao.setNome(resultSet.getString("nome"));
                condicao.setDias(resultSet.getInt("dias"));
                condicao.setParcelas(resultSet.getInt("parcelas"));
                condicao.setAtivo(resultSet.getBoolean("status"));
                condicao.setJurosPercentual(resultSet.getDouble("juros_percentual"));
                condicao.setMultaPercentual(resultSet.getDouble("multa_percentual"));
                condicao.setDescontoPercentual(resultSet.getDouble("desconto_percentual"));
                condicao.setDataCriacao(resultSet.getDate("created_at"));
                condicao.setUltimaModificacao(resultSet.getDate("updated_at"));

                ParcelaCondicaoPagamentoDAO parcelaDAO = new ParcelaCondicaoPagamentoDAO();
                condicao.setParcelasCondicao(parcelaDAO.buscarPorCondicaoPagamentoId(condicao.getId()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return condicao;
    }

    public void atualizar(CondicaoPagamento condicaoPagamento) {
        String sqlCondicao = "UPDATE condicoes_pagamento SET nome = ?, dias = ?, parcelas = ?, status = ?, juros_percentual = ?, multa_percentual = ?, desconto_percentual = ?, updated_at = NOW() WHERE id = ?";
        String sqlExcluirParcelas = "DELETE FROM parcelas_condicoes_pagamento WHERE condicoes_pagamento_id = ?";
        String sqlInserirParcela = "INSERT INTO parcelas_condicoes_pagamento (numero_parcela, dias, percentual, condicao_pagamento_id, forma_pagamento_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statementCondicao = connection.prepareStatement(sqlCondicao);
                 PreparedStatement statementExcluirParcelas = connection.prepareStatement(sqlExcluirParcelas);
                 PreparedStatement statementInserirParcela = connection.prepareStatement(sqlInserirParcela)) {

                statementCondicao.setString(1, condicaoPagamento.getNome());
                statementCondicao.setInt(2, condicaoPagamento.getDias());
                statementCondicao.setInt(3, condicaoPagamento.getParcelas());
                statementCondicao.setBoolean(4, condicaoPagamento.getAtivo() != null ? condicaoPagamento.getAtivo() : true);
                statementCondicao.setDouble(5, condicaoPagamento.getJurosPercentual());
                statementCondicao.setDouble(6, condicaoPagamento.getMultaPercentual());
                statementCondicao.setDouble(7, condicaoPagamento.getDescontoPercentual());
                statementCondicao.setLong(8, condicaoPagamento.getId());
                statementCondicao.executeUpdate();

                statementExcluirParcelas.setLong(1, condicaoPagamento.getId());
                statementExcluirParcelas.executeUpdate();

                for (ParcelaCondicaoPagamento parcela : condicaoPagamento.getParcelasCondicao()) {
                    statementInserirParcela.setInt(1, parcela.getNumeroParcela());
                    statementInserirParcela.setInt(2, parcela.getDias());
                    statementInserirParcela.setDouble(3, parcela.getPercentual());
                    statementInserirParcela.setLong(4, condicaoPagamento.getId());
                    statementInserirParcela.setLong(5, parcela.getFormaPagamento().getId());
                    statementInserirParcela.executeUpdate();
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM condicoes_pagamento WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}