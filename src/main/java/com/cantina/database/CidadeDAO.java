package com.cantina.database;

import com.cantina.entities.Cidade;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CidadeDAO {

    public Cidade salvar(Cidade cidade) {
        String sql = "INSERT INTO cidade (nome, codigo_ibge, estado_id, status, created_at, updated_at, ddd) VALUES (?, ?, ?, ?, NOW(), NOW(), ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, cidade.getNome());
            statement.setString(2, cidade.getCodigoIbge());
            statement.setLong(3, cidade.getEstadoId());
            statement.setBoolean(4, cidade.getAtivo() != null ? cidade.getAtivo() : true);
            statement.setObject(5, cidade.getDdd());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cidade;
    }

    public List<Cidade> listarTodas() {
        List<Cidade> cidades = new ArrayList<>();
        String sql = "SELECT * FROM cidade";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Cidade cidade = new Cidade();
                cidade.setId(resultSet.getLong("id"));
                cidade.setNome(resultSet.getString("nome"));
                cidade.setCodigoIbge(resultSet.getString("codigo_ibge"));
                cidade.setEstadoId(resultSet.getLong("estado_id"));
                cidade.setAtivo(resultSet.getBoolean("status"));
                cidade.setDataCriacao(resultSet.getDate("created_at"));
                cidade.setUltimaModificacao(resultSet.getDate("updated_at"));
                cidade.setDdd((Integer) resultSet.getObject("ddd"));
                cidades.add(cidade);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cidades;
    }

    public Cidade buscarPorId(Long id) {
        String sql = "SELECT * FROM cidade WHERE id = ?";
        Cidade cidade = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                cidade = new Cidade();
                cidade.setId(resultSet.getLong("id"));
                cidade.setNome(resultSet.getString("nome"));
                cidade.setCodigoIbge(resultSet.getString("codigo_ibge"));
                cidade.setEstadoId(resultSet.getLong("estado_id"));
                cidade.setAtivo(resultSet.getBoolean("status"));
                cidade.setDataCriacao(resultSet.getDate("created_at"));
                cidade.setUltimaModificacao(resultSet.getDate("updated_at"));
                cidade.setDdd((Integer) resultSet.getObject("ddd"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cidade;
    }

    public void atualizar(Cidade cidade) {
        String sql = "UPDATE cidade SET nome = ?, codigo_ibge = ?, estado_id = ?, status = ?, updated_at = NOW(), ddd = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, cidade.getNome());
            statement.setString(2, cidade.getCodigoIbge());
            statement.setLong(3, cidade.getEstadoId());
            statement.setBoolean(4, cidade.getAtivo() != null ? cidade.getAtivo() : true);
            statement.setObject(5, cidade.getDdd());
            statement.setLong(6, cidade.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM cidade WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}