package com.cantina.database;

import com.cantina.entities.Fornecedor;
import com.cantina.exceptions.DuplicateCnpjException;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FornecedorDAO {

    public void salvar(Fornecedor fornecedor) {
        String sql = "INSERT INTO fornecedor (tipo, razao_social, nome_fantasia, cpf_cnpj, email, telefone, endereco, numero, complemento, bairro, cep, cidade_id, rg_inscricao_estadual, status, condicao_pagamento_id, limite_credito, created_at, updated_at, observacao) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, fornecedor.getTipo());
            statement.setString(2, fornecedor.getRazaoSocial());
            statement.setString(3, fornecedor.getNomeFantasia());
            statement.setString(4, fornecedor.getCpfCnpj());
            statement.setString(5, fornecedor.getEmail());
            statement.setString(6, fornecedor.getTelefone());
            statement.setString(7, fornecedor.getEndereco());
            statement.setString(8, fornecedor.getNumero());
            statement.setString(9, fornecedor.getComplemento());
            statement.setString(10, fornecedor.getBairro());
            statement.setString(11, fornecedor.getCep());
            statement.setObject(12, fornecedor.getCidadeId());
            statement.setString(13, fornecedor.getRgInscricaoEstadual());
            statement.setBoolean(14, fornecedor.getAtivo() != null ? fornecedor.getAtivo() : true);
            statement.setObject(15, fornecedor.getCondicaoPagamentoId());
            statement.setBigDecimal(16, fornecedor.getLimiteCredito());
            statement.setString(17, fornecedor.getObservacao());
            statement.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage().contains("fornecedor.cnpj")) {
                throw new DuplicateCnpjException("Já existe um fornecedor cadastrado com este CNPJ.");
            }
            throw new RuntimeException("Erro inesperado ao salvar o fornecedor.", e);
        }
    }

    public List<Fornecedor> listarTodos() {
        List<Fornecedor> fornecedores = new ArrayList<>();
        String sql = "SELECT * FROM fornecedor";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Fornecedor fornecedor = new Fornecedor();
                fornecedor.setId(resultSet.getLong("id"));
                fornecedor.setTipo((Integer) resultSet.getObject("tipo"));
                fornecedor.setRazaoSocial(resultSet.getString("razao_social"));
                fornecedor.setNomeFantasia(resultSet.getString("nome_fantasia"));
                fornecedor.setCpfCnpj(resultSet.getString("cpf_cnpj"));
                fornecedor.setEmail(resultSet.getString("email"));
                fornecedor.setTelefone(resultSet.getString("telefone"));
                fornecedor.setEndereco(resultSet.getString("endereco"));
                fornecedor.setNumero(resultSet.getString("numero"));
                fornecedor.setComplemento(resultSet.getString("complemento"));
                fornecedor.setBairro(resultSet.getString("bairro"));
                fornecedor.setCep(resultSet.getString("cep"));
                fornecedor.setCidadeId((Long) resultSet.getObject("cidade_id"));
                fornecedor.setRgInscricaoEstadual(resultSet.getString("rg_inscricao_estadual"));
                fornecedor.setAtivo(resultSet.getBoolean("status"));
                fornecedor.setCondicaoPagamentoId((Long) resultSet.getObject("condicao_pagamento_id"));
                fornecedor.setLimiteCredito(resultSet.getBigDecimal("limite_credito"));
                fornecedor.setObservacao(resultSet.getString("observacao"));
                fornecedor.setDataCriacao(resultSet.getTimestamp("created_at"));
                fornecedor.setUltimaModificacao(resultSet.getTimestamp("updated_at"));
                fornecedores.add(fornecedor);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fornecedores;
    }

    public Fornecedor buscarPorId(Long id) {
        String sql = "SELECT * FROM fornecedor WHERE id = ?";
        Fornecedor fornecedor = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                fornecedor = new Fornecedor();
                fornecedor.setId(resultSet.getLong("id"));
                fornecedor.setTipo((Integer) resultSet.getObject("tipo"));
                fornecedor.setRazaoSocial(resultSet.getString("razao_social"));
                fornecedor.setNomeFantasia(resultSet.getString("nome_fantasia"));
                fornecedor.setCpfCnpj(resultSet.getString("cpf_cnpj"));
                fornecedor.setEmail(resultSet.getString("email"));
                fornecedor.setTelefone(resultSet.getString("telefone"));
                fornecedor.setEndereco(resultSet.getString("endereco"));
                fornecedor.setNumero(resultSet.getString("numero"));
                fornecedor.setComplemento(resultSet.getString("complemento"));
                fornecedor.setBairro(resultSet.getString("bairro"));
                fornecedor.setCep(resultSet.getString("cep"));
                fornecedor.setCidadeId((Long) resultSet.getObject("cidade_id"));
                fornecedor.setRgInscricaoEstadual(resultSet.getString("rg_inscricao_estadual"));
                fornecedor.setAtivo(resultSet.getBoolean("status"));
                fornecedor.setCondicaoPagamentoId((Long) resultSet.getObject("condicao_pagamento_id"));
                fornecedor.setLimiteCredito(resultSet.getBigDecimal("limite_credito"));
                fornecedor.setObservacao(resultSet.getString("observacao"));
                fornecedor.setDataCriacao(resultSet.getTimestamp("created_at"));
                fornecedor.setUltimaModificacao(resultSet.getTimestamp("updated_at"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fornecedor;
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM fornecedor WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void update(Fornecedor fornecedor) {
        String sql = "UPDATE fornecedor SET tipo = ?, razao_social = ?, nome_fantasia = ?, cpf_cnpj = ?, email = ?, telefone = ?, endereco = ?, numero = ?, complemento = ?, bairro =?, cep = ?, cidade_id = ?, rg_inscricao_estadual = ?, status = ?, condicao_pagamento_id = ?, limite_credito = ?, updated_at = NOW(), observacao = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, fornecedor.getTipo());
            statement.setString(2, fornecedor.getRazaoSocial());
            statement.setString(3, fornecedor.getNomeFantasia());
            statement.setString(4, fornecedor.getCpfCnpj());
            statement.setString(5, fornecedor.getEmail());
            statement.setString(6, fornecedor.getTelefone());
            statement.setString(7, fornecedor.getEndereco());
            statement.setString(8, fornecedor.getNumero());
            statement.setString(9, fornecedor.getComplemento());
            statement.setString(10, fornecedor.getBairro());
            statement.setString(11, fornecedor.getCep());
            statement.setObject(12, fornecedor.getCidadeId());
            statement.setString(13, fornecedor.getRgInscricaoEstadual());
            statement.setBoolean(14, fornecedor.getAtivo() != null ? fornecedor.getAtivo() : true);
            statement.setObject(15, fornecedor.getCondicaoPagamentoId());
            statement.setBigDecimal(16, fornecedor.getLimiteCredito());
            statement.setString(17, fornecedor.getObservacao());
            statement.setLong(18, fornecedor.getId());
            statement.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage().contains("fornecedor.cnpj")) {
                throw new DuplicateCnpjException("Já existe um fornecedor cadastrado com este CNPJ.");
            }
            throw new RuntimeException("Erro ao atualizar o fornecedor.", e);
        }
    }
}