package com.cantina.database;

import com.cantina.entities.Estado;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EstadoDAO {

    public Estado salvar(Estado estado) {
        String sql = "INSERT INTO estados (nome, uf, pais_id) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, estado.getNome());
            statement.setString(2, estado.getUf());
            statement.setString(3, estado.getPaisId());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                estado.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estado;
    }

    public List<Estado> listarTodos() {
        List<Estado> estados = new ArrayList<>();
        String sql = "SELECT * FROM estados";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Estado estado = new Estado();
                estado.setId(resultSet.getLong("id"));
                estado.setNome(resultSet.getString("nome"));
                estado.setUf(resultSet.getString("uf"));
                estado.setPaisId(resultSet.getString("pais_id"));
                estados.add(estado);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return estados;
    }

    public Estado buscarPorId(Long id) {
        String sql = "SELECT * FROM estados WHERE id = ?";
        Estado estado = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                estado = new Estado();
                estado.setId(resultSet.getLong("id"));
                estado.setNome(resultSet.getString("nome"));
                estado.setUf(resultSet.getString("uf"));
                estado.setPaisId(resultSet.getString("pais_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return estado;
    }

    public void atualizar(Estado estado) {
        String sql = "UPDATE estados SET nome = ?, uf = ?, pais_id = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, estado.getNome());
            statement.setString(2, estado.getUf());
            statement.setString(3, estado.getPaisId());
            statement.setLong(4, estado.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM estados WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}