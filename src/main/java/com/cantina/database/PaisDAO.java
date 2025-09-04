package com.cantina.database;

import com.cantina.entities.Pais;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PaisDAO {

    public Pais salvar(Pais pais) {
        String sql = "INSERT INTO pais (nome, sigla, ddi, status, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())";
        String generatedColumns[] = { "id" };

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, generatedColumns)) {

            statement.setString(1, pais.getNome());
            statement.setString(2, pais.getSigla());
            statement.setString(3, pais.getDdi());
            statement.setBoolean(4, pais.getAtivo() != null ? pais.getAtivo() : true);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pais.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pais;
    }

    public List<Pais> listarTodos() {
        List<Pais> paises = new ArrayList<>();
        String sql = "SELECT * FROM pais";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Pais pais = new Pais();
                pais.setId(resultSet.getLong("id"));
                pais.setNome(resultSet.getString("nome"));
                pais.setSigla(resultSet.getString("sigla"));
                pais.setDdi(resultSet.getString("ddi"));
                pais.setAtivo(resultSet.getBoolean("status"));
                pais.setDataCriacao(resultSet.getDate("created_at"));
                pais.setUltimaModificacao(resultSet.getDate("updated_at"));
                paises.add(pais);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return paises;
    }

    public Pais buscarPorId(Long id) {
        String sql = "SELECT * FROM pais WHERE id = ?";
        Pais pais = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                pais = new Pais();
                pais.setId(resultSet.getLong("id"));
                pais.setNome(resultSet.getString("nome"));
                pais.setSigla(resultSet.getString("sigla"));
                pais.setDdi(resultSet.getString("ddi"));
                pais.setAtivo(resultSet.getBoolean("status"));
                pais.setDataCriacao(resultSet.getDate("created_at"));
                pais.setUltimaModificacao(resultSet.getDate("updated_at"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pais;
    }

    public Pais atualizar(Long id, Pais pais) {
        String sql = "UPDATE pais SET nome = ?, sigla = ?, ddi = ?, status = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, pais.getNome());
            statement.setString(2, pais.getSigla());
            statement.setString(3, pais.getDdi());
            statement.setBoolean(4, pais.getAtivo() != null ? pais.getAtivo() : true);
            statement.setLong(5, id);
            statement.executeUpdate();

            return buscarPorId(id);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM pais WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}