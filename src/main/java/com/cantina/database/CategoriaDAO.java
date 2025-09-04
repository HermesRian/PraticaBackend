package com.cantina.database;

import com.cantina.entities.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public void salvar(Categoria categoria) {
        String sql = "INSERT INTO categoria (nome, status, created_at, updated_at) VALUES (?, ?, NOW(), NOW())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, categoria.getNome());
            statement.setBoolean(2, categoria.getAtivo() != null ? categoria.getAtivo() : true);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Categoria> listarTodos() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categoria";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(resultSet.getInt("Id"));
                categoria.setNome(resultSet.getString("nome"));
                categoria.setAtivo(resultSet.getBoolean("status"));
                categoria.setDataCriacao(resultSet.getDate("created_at"));
                categoria.setUltimaModificacao(resultSet.getDate("updated_at"));

                categorias.add(categoria);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categorias;
    }

    public Categoria buscarPorId(Integer id) {
        String sql = "SELECT * FROM categoria WHERE Id = ?";
        Categoria categoria = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                categoria = new Categoria();
                categoria.setId(resultSet.getInt("Id"));
                categoria.setNome(resultSet.getString("nome"));
                categoria.setAtivo(resultSet.getBoolean("status"));
                categoria.setDataCriacao(resultSet.getDate("created_at"));
                categoria.setUltimaModificacao(resultSet.getDate("updated_at"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categoria;
    }

    public void atualizar(Categoria categoria) {
        String sql = "UPDATE categoria SET nome = ?, status = ?, updated_at = NOW() WHERE Id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, categoria.getNome());
            statement.setBoolean(2, categoria.getAtivo() != null ? categoria.getAtivo() : true);
            statement.setInt(3, categoria.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(Integer id) {
        String sql = "DELETE FROM categoria WHERE Id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}