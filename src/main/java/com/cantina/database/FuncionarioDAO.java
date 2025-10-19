package com.cantina.database;

import com.cantina.entities.Funcionario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {

    public void salvar(Funcionario funcionario) {
        String sql = "INSERT INTO funcionarios (nome, cargo, salario, email, telefone, endereco, numero, complemento, bairro, cep, cidade_id, ativo, data_admissao, data_demissao, apelido, rg_inscricao_estadual, cnh, data_validade_cnh, sexo, observacao, estado_civil, is_brasileiro, nacionalidade, data_nascimento, funcao_funcionario_id, cpf_cnpj, created_at, updated_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";


        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, funcionario.getNome());
            statement.setString(2, funcionario.getCargo());
            statement.setBigDecimal(3, funcionario.getSalario());
            statement.setString(4, funcionario.getEmail());
            statement.setString(5, funcionario.getTelefone());
            statement.setString(6, funcionario.getEndereco());
            statement.setString(7, funcionario.getNumero());
            statement.setString(8, funcionario.getComplemento());
            statement.setString(9, funcionario.getBairro());
            statement.setString(10, funcionario.getCep());
            statement.setLong(11, funcionario.getCidadeId());
            statement.setBoolean(12, funcionario.getAtivo());
            statement.setDate(13, funcionario.getDataAdmissao() != null ? new java.sql.Date(funcionario.getDataAdmissao().getTime()) : null);
            statement.setDate(14, funcionario.getDataDemissao() != null ? new java.sql.Date(funcionario.getDataDemissao().getTime()) : null);
            statement.setString(15, funcionario.getApelido());
            statement.setString(16, funcionario.getRgInscricaoEstadual());
            statement.setString(17, funcionario.getCnh());
            statement.setDate(18, funcionario.getDataValidadeCnh() != null ? new java.sql.Date(funcionario.getDataValidadeCnh().getTime()) : null);
            statement.setObject(19, funcionario.getSexo());
            statement.setString(20, funcionario.getObservacao());
            statement.setObject(21, funcionario.getEstadoCivil());
            statement.setObject(22, funcionario.getIsBrasileiro());
            statement.setObject(23, funcionario.getNacionalidade());
            statement.setDate(24, funcionario.getDataNascimento() != null ? new java.sql.Date(funcionario.getDataNascimento().getTime()) : null);
            statement.setObject(25, funcionario.getFuncaoFuncionarioId());
            statement.setString(26, funcionario.getCpfCnpj());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Funcionario> listarTodos() {
        List<Funcionario> funcionarios = new ArrayList<>();
        String sql = "SELECT * FROM funcionarios";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Funcionario funcionario = mapResultSetToFuncionario(resultSet);
                funcionarios.add(funcionario);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return funcionarios;
    }

    public Funcionario buscarPorId(Long id) {
        String sql = "SELECT * FROM funcionarios WHERE id = ?";
        Funcionario funcionario = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                funcionario = mapResultSetToFuncionario(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return funcionario;
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM funcionarios WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void atualizar(Funcionario funcionario) {
        String sql = "UPDATE funcionarios SET nome = ?, cargo = ?, salario = ?, email = ?, telefone = ?, endereco = ?, numero = ?, complemento = ?, bairro = ?, cep = ?, cidade_id = ?, ativo = ?, data_admissao = ?, data_demissao = ?, apelido = ?, rg_inscricao_estadual = ?, cnh = ?, data_validade_cnh = ?, sexo = ?, observacao = ?, estado_civil = ?, is_brasileiro = ?, nacionalidade = ?, data_nascimento = ?, funcao_funcionario_id = ?, cpf_cnpj = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, funcionario.getNome());
            statement.setString(2, funcionario.getCargo());
            statement.setBigDecimal(3, funcionario.getSalario());
            statement.setString(4, funcionario.getEmail());
            statement.setString(5, funcionario.getTelefone());
            statement.setString(6, funcionario.getEndereco());
            statement.setString(7, funcionario.getNumero());
            statement.setString(8, funcionario.getComplemento());
            statement.setString(9, funcionario.getBairro());
            statement.setString(10, funcionario.getCep());
            statement.setLong(11, funcionario.getCidadeId());
            statement.setBoolean(12, funcionario.getAtivo());
            statement.setDate(13, funcionario.getDataAdmissao() != null ? new java.sql.Date(funcionario.getDataAdmissao().getTime()) : null);
            statement.setDate(14, funcionario.getDataDemissao() != null ? new java.sql.Date(funcionario.getDataDemissao().getTime()) : null);
            statement.setString(15, funcionario.getApelido());
            statement.setString(16, funcionario.getRgInscricaoEstadual());
            statement.setString(17, funcionario.getCnh());
            statement.setDate(18, funcionario.getDataValidadeCnh() != null ? new java.sql.Date(funcionario.getDataValidadeCnh().getTime()) : null);
            statement.setObject(19, funcionario.getSexo());
            statement.setString(20, funcionario.getObservacao());
            statement.setObject(21, funcionario.getEstadoCivil());
            statement.setObject(22, funcionario.getIsBrasileiro());
            statement.setObject(23, funcionario.getNacionalidade());
            statement.setDate(24, funcionario.getDataNascimento() != null ? new java.sql.Date(funcionario.getDataNascimento().getTime()) : null);
            statement.setObject(25, funcionario.getFuncaoFuncionarioId());
            statement.setString(26, funcionario.getCpfCnpj());
            statement.setLong(27, funcionario.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Funcionario mapResultSetToFuncionario(ResultSet resultSet) throws SQLException {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(resultSet.getLong("id"));
        funcionario.setNome(resultSet.getString("nome"));
        funcionario.setCargo(resultSet.getString("cargo"));
        funcionario.setSalario(resultSet.getBigDecimal("salario"));
        funcionario.setEmail(resultSet.getString("email"));
        funcionario.setTelefone(resultSet.getString("telefone"));
        funcionario.setEndereco(resultSet.getString("endereco"));
        funcionario.setNumero(resultSet.getString("numero"));
        funcionario.setComplemento(resultSet.getString("complemento"));
        funcionario.setBairro(resultSet.getString("bairro"));
        funcionario.setCep(resultSet.getString("cep"));
        funcionario.setCidadeId(resultSet.getLong("cidade_id"));
        funcionario.setAtivo(resultSet.getBoolean("ativo"));
        funcionario.setDataAdmissao(resultSet.getDate("data_admissao"));
        funcionario.setDataDemissao(resultSet.getDate("data_demissao"));
        funcionario.setApelido(resultSet.getString("apelido"));
        funcionario.setDataCriacao(resultSet.getDate("created_at"));
        funcionario.setDataAlteracao(resultSet.getDate("updated_at"));

        funcionario.setRgInscricaoEstadual(resultSet.getString("rg_inscricao_estadual"));
        funcionario.setCnh(resultSet.getString("cnh"));
        funcionario.setDataValidadeCnh(resultSet.getDate("data_validade_cnh"));
        funcionario.setSexo((Integer) resultSet.getObject("sexo"));
        funcionario.setObservacao(resultSet.getString("observacao"));
        funcionario.setEstadoCivil((Integer) resultSet.getObject("estado_civil"));
        funcionario.setIsBrasileiro((Integer) resultSet.getObject("is_brasileiro"));
        funcionario.setNacionalidade((Integer) resultSet.getObject("nacionalidade"));
        funcionario.setDataNascimento(resultSet.getDate("data_nascimento"));
        funcionario.setFuncaoFuncionarioId((Long) resultSet.getObject("funcao_funcionario_id"));
        funcionario.setCpfCnpj(resultSet.getString("cpf_cnpj"));
        return funcionario;
    }
}