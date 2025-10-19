package com.cantina.database;

import com.cantina.entities.UnidadeMedida;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UnidadeMedidaDAO {

    public void salvar(UnidadeMedida unidadeMedida) {
        String sql = "INSERT INTO unidades_medida (nome, status, created_at, updated_at) VALUES (?, ?, NOW(), NOW())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, unidadeMedida.getNome());
            statement.setBoolean(2, unidadeMedida.getAtivo() != null ? unidadeMedida.getAtivo() : true);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<UnidadeMedida> listarTodos() {
        List<UnidadeMedida> unidadesMedida = new ArrayList<>();
        String sql = "SELECT * FROM unidades_medida";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                UnidadeMedida unidadeMedida = new UnidadeMedida();
                unidadeMedida.setId(resultSet.getLong("id"));
                unidadeMedida.setNome(resultSet.getString("nome"));
                unidadeMedida.setAtivo(resultSet.getBoolean("status"));
                unidadeMedida.setDataCriacao(resultSet.getTimestamp("created_at"));
                unidadeMedida.setUltimaModificacao(resultSet.getTimestamp("updated_at"));

                unidadesMedida.add(unidadeMedida);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return unidadesMedida;
    }

    public UnidadeMedida buscarPorId(Long id) {
        String sql = "SELECT * FROM unidades_medida WHERE id = ?";
        UnidadeMedida unidadeMedida = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                unidadeMedida = new UnidadeMedida();
                unidadeMedida.setId(resultSet.getLong("id"));
                unidadeMedida.setNome(resultSet.getString("nome"));
                unidadeMedida.setAtivo(resultSet.getBoolean("status"));
                unidadeMedida.setDataCriacao(resultSet.getTimestamp("created_at"));
                unidadeMedida.setUltimaModificacao(resultSet.getTimestamp("updated_at"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return unidadeMedida;
    }

    public void atualizar(UnidadeMedida unidadeMedida) {
        String sql = "UPDATE unidades_medida SET nome = ?, status = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, unidadeMedida.getNome());
            statement.setBoolean(2, unidadeMedida.getAtivo() != null ? unidadeMedida.getAtivo() : true);
            statement.setLong(3, unidadeMedida.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM unidades_medida WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}