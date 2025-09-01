package com.cantina.database;

import com.cantina.entities.Marca;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarcaDAO {

    public void salvar(Marca marca) {
        String sql = "INSERT INTO marca (nome, ativo) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, marca.getNome());
            statement.setBoolean(2, marca.getAtivo() != null ? marca.getAtivo() : true);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Marca> listarTodos() {
        List<Marca> marcas = new ArrayList<>();
        String sql = "SELECT * FROM marca";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Marca marca = new Marca();
                marca.setId(resultSet.getLong("id"));
                marca.setNome(resultSet.getString("nome"));
                marca.setAtivo(resultSet.getBoolean("ativo"));
                marca.setDataCriacao(resultSet.getTimestamp("data_criacao"));
                marca.setUltimaModificacao(resultSet.getTimestamp("ultima_modificacao"));

                marcas.add(marca);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return marcas;
    }

    public Marca buscarPorId(Long id) {
        String sql = "SELECT * FROM marca WHERE id = ?";
        Marca marca = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                marca = new Marca();
                marca.setId(resultSet.getLong("id"));
                marca.setNome(resultSet.getString("nome"));
                marca.setAtivo(resultSet.getBoolean("ativo"));
                marca.setDataCriacao(resultSet.getTimestamp("data_criacao"));
                marca.setUltimaModificacao(resultSet.getTimestamp("ultima_modificacao"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return marca;
    }

    public void atualizar(Marca marca) {
        String sql = "UPDATE marca SET nome = ?, ativo = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, marca.getNome());
            statement.setBoolean(2, marca.getAtivo() != null ? marca.getAtivo() : true);
            statement.setLong(3, marca.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM marca WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}