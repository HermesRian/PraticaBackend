package com.cantina.database;

import com.cantina.entities.Transportadora;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransportadoraDAO {

    public void salvar(Transportadora transportadora) {
        String sql = "INSERT INTO transportadora (razao_social, nome_fantasia, cnpj, email, telefone, endereco, numero, complemento, bairro, cidade_id, cep, tipo,  rg_inscricao_estadual, condicao_pagamento_id, observacao, ativo, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, transportadora.getRazaoSocial());
            statement.setString(2, transportadora.getNomeFantasia());
            statement.setString(3, transportadora.getCnpj());
            statement.setString(4, transportadora.getEmail());
            statement.setString(5, transportadora.getTelefone());
            statement.setString(6, transportadora.getEndereco());
            statement.setString(7, transportadora.getNumero());
            statement.setString(8, transportadora.getComplemento());
            statement.setString(9, transportadora.getBairro());
            statement.setObject(10, transportadora.getCidadeId());
            statement.setString(11, transportadora.getCep());
            statement.setString(12, transportadora.getTipo() != null ? transportadora.getTipo() : "J");
            statement.setString(13, transportadora.getRgInscricaoEstadual());
            statement.setObject(14, transportadora.getCondicaoPagamentoId());
            statement.setString(15, transportadora.getObservacao());
            statement.setBoolean(16, transportadora.getAtivo() != null ? transportadora.getAtivo() : true);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Transportadora> listarTodas() {
        List<Transportadora> transportadoras = new ArrayList<>();
        String sql = "SELECT * FROM transportadora";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Transportadora transportadora = new Transportadora();
                transportadora.setId(resultSet.getLong("id"));
                transportadora.setRazaoSocial(resultSet.getString("razao_social"));
                transportadora.setNomeFantasia(resultSet.getString("nome_fantasia"));
                transportadora.setCnpj(resultSet.getString("cnpj"));
                transportadora.setEmail(resultSet.getString("email"));
                transportadora.setTelefone(resultSet.getString("telefone"));
                transportadora.setEndereco(resultSet.getString("endereco"));
                transportadora.setNumero(resultSet.getString("numero"));
                transportadora.setComplemento(resultSet.getString("complemento"));
                transportadora.setBairro(resultSet.getString("bairro"));
                transportadora.setCidadeId(resultSet.getObject("cidade_id", Long.class));
                transportadora.setCep(resultSet.getString("cep"));
                transportadora.setTipo(resultSet.getString("tipo"));
                transportadora.setRgInscricaoEstadual(resultSet.getString("rg_inscricao_estadual"));
                transportadora.setCondicaoPagamentoId(resultSet.getObject("condicao_pagamento_id", Long.class));
                transportadora.setObservacao(resultSet.getString("observacao"));
                transportadora.setAtivo(resultSet.getBoolean("ativo"));
                transportadora.setDataCriacao(resultSet.getTimestamp("created_at"));
                transportadora.setUltimaModificacao(resultSet.getTimestamp("updated_at"));
                transportadoras.add(transportadora);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transportadoras;
    }

    public Transportadora buscarPorId(Long id) {
        String sql = "SELECT * FROM transportadora WHERE id = ?";
        Transportadora transportadora = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                transportadora = new Transportadora();
                transportadora.setId(resultSet.getLong("id"));
                transportadora.setRazaoSocial(resultSet.getString("razao_social"));
                transportadora.setNomeFantasia(resultSet.getString("nome_fantasia"));
                transportadora.setCnpj(resultSet.getString("cnpj"));
                transportadora.setEmail(resultSet.getString("email"));
                transportadora.setTelefone(resultSet.getString("telefone"));
                transportadora.setEndereco(resultSet.getString("endereco"));
                transportadora.setNumero(resultSet.getString("numero"));
                transportadora.setComplemento(resultSet.getString("complemento"));
                transportadora.setBairro(resultSet.getString("bairro"));
                transportadora.setCidadeId(resultSet.getObject("cidade_id", Long.class));
                transportadora.setCep(resultSet.getString("cep"));
                transportadora.setTipo(resultSet.getString("tipo"));
                transportadora.setRgInscricaoEstadual(resultSet.getString("rg_inscricao_estadual"));
                transportadora.setCondicaoPagamentoId(resultSet.getObject("condicao_pagamento_id", Long.class));
                transportadora.setObservacao(resultSet.getString("observacao"));
                transportadora.setAtivo(resultSet.getBoolean("ativo"));
                transportadora.setDataCriacao(resultSet.getTimestamp("created_at"));
                transportadora.setUltimaModificacao(resultSet.getTimestamp("updated_at"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transportadora;
    }

    public void atualizar(Transportadora transportadora) {
        String sql = "UPDATE transportadora SET razao_social = ?, nome_fantasia = ?, cnpj = ?, email = ?, telefone = ?, endereco = ?, numero = ?, complemento = ?, bairro = ?, cidade_id = ?, cep = ?, tipo = ?, rg_inscricao_estadual = ?, condicao_pagamento_id = ?, observacao = ?, ativo = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, transportadora.getRazaoSocial());
            statement.setString(2, transportadora.getNomeFantasia());
            statement.setString(3, transportadora.getCnpj());
            statement.setString(4, transportadora.getEmail());
            statement.setString(5, transportadora.getTelefone());
            statement.setString(6, transportadora.getEndereco());
            statement.setString(7, transportadora.getNumero());
            statement.setString(8, transportadora.getComplemento());
            statement.setString(9, transportadora.getBairro());
            statement.setObject(10, transportadora.getCidadeId());
            statement.setString(11, transportadora.getCep());
            statement.setString(12, transportadora.getTipo() != null ? transportadora.getTipo() : "J");
            statement.setString(13, transportadora.getRgInscricaoEstadual());
            statement.setObject(14, transportadora.getCondicaoPagamentoId());
            statement.setString(15, transportadora.getObservacao());
            statement.setBoolean(16, transportadora.getAtivo() != null ? transportadora.getAtivo() : true);
            statement.setLong(17, transportadora.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM transportadora WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
