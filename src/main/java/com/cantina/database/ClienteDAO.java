package com.cantina.database;

import com.cantina.entities.Cliente;
import com.cantina.exceptions.DuplicateCnpjCpfException;
import com.cantina.exceptions.InvalidDocumentException;
import com.cantina.utils.DocumentValidationUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class ClienteDAO {

    public void salvar(Cliente cliente) {
        if (cliente.getTipo() != null) {
            boolean isBrasileiro = isClienteBrasileiro(cliente.getCidadeId());
            boolean documentoValido = DocumentValidationUtil.validarCpfCnpjComNacionalidade(
                cliente.getCnpjCpf(), cliente.getTipo(), isBrasileiro);
            if (!documentoValido) {
                String tipoDocumento = cliente.getTipo() == 0 ? "CPF" : "CNPJ";
                throw new InvalidDocumentException(tipoDocumento + " inválido");
            }
        }

        String sql = "INSERT INTO cliente (nome, cnpjCpf, endereco, numero, complemento, bairro, cep, cidade_id, telefone, email, status, " +
                "apelido, limite_credito, nacionalidade, rg_inscricao_estadual, data_nascimento, estado_civil, tipo, sexo, " +
                "condicao_pagamento_id, observacao, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, cliente.getNome());
            statement.setString(2, cliente.getCnpjCpf());
            statement.setString(3, cliente.getEndereco());
            statement.setString(4, cliente.getNumero());
            statement.setString(5, cliente.getComplemento());
            statement.setString(6, cliente.getBairro());
            statement.setString(7, cliente.getCep());
            statement.setObject(8, cliente.getCidadeId());
            statement.setString(9, cliente.getTelefone());
            statement.setString(10, cliente.getEmail());

            if (cliente.getAtivo() != null) {
                statement.setBoolean(11, cliente.getAtivo());
            } else {
                statement.setBoolean(11, true);
            }

            statement.setString(12, cliente.getApelido());
            statement.setBigDecimal(13, cliente.getLimiteCredito() != null ? cliente.getLimiteCredito() : BigDecimal.ZERO);
            statement.setString(14, cliente.getNacionalidade());
            statement.setString(15, cliente.getRgInscricaoEstadual());

            if (cliente.getDataNascimento() != null) {
                statement.setDate(16, new java.sql.Date(cliente.getDataNascimento().getTime()));
            } else {
                statement.setNull(16, Types.DATE);
            }

            statement.setString(17, cliente.getEstadoCivil());

            if (cliente.getTipo() != null) {
                statement.setInt(18, cliente.getTipo());
            } else {
                statement.setNull(18, Types.INTEGER);
            }

            statement.setString(19, cliente.getSexo());

            if (cliente.getCondicaoPagamentoId() != null) {
                statement.setLong(20, cliente.getCondicaoPagamentoId());
            } else {
                statement.setNull(20, Types.BIGINT);
            }

            statement.setString(21, cliente.getObservacao());

            statement.executeUpdate();
        }
        catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("unique") || e.getMessage().toLowerCase().contains("duplicate")) {
                String campo = "CNPJ/CPF";
                if (cliente.getTipo() != null && cliente.getTipo() == 0) {
                    campo = "CPF";
                } else if (cliente.getTipo() != null && cliente.getTipo() == 1) {
                    campo = "CNPJ";
                }
                throw new DuplicateCnpjCpfException("Já existe um cliente cadastrado com este " + campo + ".");
            }
            throw new RuntimeException("Erro inesperado ao salvar o cliente.", e);
        }
    }

    private boolean isClienteBrasileiro(Long cidadeId) {
        if (cidadeId == null) return false;

        String sql = "SELECT p.nome FROM cidade c " +
                     "JOIN estado e ON c.estado_id = e.id " +
                     "JOIN pais p ON e.pais_id = p.id " +
                     "WHERE c.id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, cidadeId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String nomePais = resultSet.getString("nome");
                return "BRASIL".equalsIgnoreCase(nomePais);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Cliente mapearClienteDoResultSet(ResultSet resultSet) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(resultSet.getLong("id"));
        cliente.setNome(resultSet.getString("nome"));
        cliente.setCnpjCpf(resultSet.getString("cnpjCpf"));
        cliente.setEndereco(resultSet.getString("endereco"));
        cliente.setNumero(resultSet.getString("numero"));
        cliente.setComplemento(resultSet.getString("complemento"));
        cliente.setBairro(resultSet.getString("bairro"));
        cliente.setCep(resultSet.getString("cep"));
        cliente.setCidadeId(resultSet.getLong("cidade_id"));
        cliente.setTelefone(resultSet.getString("telefone"));
        cliente.setEmail(resultSet.getString("email"));
        cliente.setAtivo(resultSet.getBoolean("status"));
        cliente.setApelido(resultSet.getString("apelido"));
        cliente.setLimiteCredito(resultSet.getBigDecimal("limite_credito"));
        cliente.setNacionalidade(resultSet.getString("nacionalidade"));
        cliente.setRgInscricaoEstadual(resultSet.getString("rg_inscricao_estadual"));
        cliente.setDataNascimento(resultSet.getDate("data_nascimento"));
        cliente.setEstadoCivil(resultSet.getString("estado_civil"));
        cliente.setTipo(resultSet.getInt("tipo"));
        if (resultSet.wasNull()) cliente.setTipo(null);
        cliente.setSexo(resultSet.getString("sexo"));
        cliente.setCondicaoPagamentoId(resultSet.getLong("condicao_pagamento_id"));
        if (resultSet.wasNull()) cliente.setCondicaoPagamentoId(null);
        cliente.setObservacao(resultSet.getString("observacao"));
        cliente.setDataCadastro(resultSet.getDate("created_at"));
        cliente.setUltimaModificacao(resultSet.getDate("updated_at"));

        return cliente;
    }

    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                clientes.add(mapearClienteDoResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientes;
    }

    public Cliente buscarPorId(Long id) {
        String sql = "SELECT * FROM cliente WHERE id = ?";
        Cliente cliente = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                cliente = mapearClienteDoResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cliente;
    }

    public void atualizar(Cliente cliente) {
        if (cliente.getTipo() != null) {
            boolean isBrasileiro = isClienteBrasileiro(cliente.getCidadeId());
            boolean documentoValido = DocumentValidationUtil.validarCpfCnpjComNacionalidade(
                cliente.getCnpjCpf(), cliente.getTipo(), isBrasileiro);
            if (!documentoValido) {
                String tipoDocumento = cliente.getTipo() == 0 ? "CPF" : "CNPJ";
                throw new InvalidDocumentException(tipoDocumento + " inválido");
            }
        }

        String sql = "UPDATE cliente SET nome = ?, cnpjCpf = ?, endereco = ?, numero = ?, complemento = ?, bairro = ?, cep = ?, " +
                "cidade_id = ?, telefone = ?, email = ?, status = ?, apelido = ?, limite_credito = ?, nacionalidade = ?, " +
                "rg_inscricao_estadual = ?, data_nascimento = ?, estado_civil = ?, tipo = ?, sexo = ?, " +
                "condicao_pagamento_id = ?, observacao = ?, updated_at = NOW() " +
                "WHERE id = ?";


        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, cliente.getNome());
            statement.setString(2, cliente.getCnpjCpf());
            statement.setString(3, cliente.getEndereco());
            statement.setString(4, cliente.getNumero());
            statement.setString(5, cliente.getComplemento());
            statement.setString(6, cliente.getBairro());
            statement.setString(7, cliente.getCep());
            statement.setObject(8, cliente.getCidadeId());
            statement.setString(9, cliente.getTelefone());
            statement.setString(10, cliente.getEmail());

            if (cliente.getAtivo() != null) {
                statement.setBoolean(11, cliente.getAtivo());
            } else {
                statement.setBoolean(11, true);
            }
            statement.setString(12, cliente.getApelido());
            statement.setBigDecimal(13, cliente.getLimiteCredito() != null ? cliente.getLimiteCredito() : BigDecimal.ZERO);
            statement.setString(14, cliente.getNacionalidade());
            statement.setString(15, cliente.getRgInscricaoEstadual());
            if (cliente.getDataNascimento() != null) {
                statement.setDate(16, new java.sql.Date(cliente.getDataNascimento().getTime()));
            } else {
                statement.setNull(16, Types.DATE);
            }
            statement.setString(17, cliente.getEstadoCivil());
            if (cliente.getTipo() != null) {
                statement.setInt(18, cliente.getTipo());
            } else {
                statement.setNull(18, Types.INTEGER);
            }
            statement.setString(19, cliente.getSexo());
            if (cliente.getCondicaoPagamentoId() != null) {
                statement.setLong(20, cliente.getCondicaoPagamentoId());
            } else {
                statement.setNull(20, Types.BIGINT);
            }

            statement.setString(21, cliente.getObservacao());
            statement.setLong(22, cliente.getId());

            statement.executeUpdate();
        }
        catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("unique") || e.getMessage().toLowerCase().contains("duplicate")) {
                String campo = "CNPJ/CPF";
                if (cliente.getTipo() != null && cliente.getTipo() == 0) {
                    campo = "CPF";
                } else if (cliente.getTipo() != null && cliente.getTipo() == 1) {
                    campo = "CNPJ";
                }
                throw new DuplicateCnpjCpfException("Já existe um cliente cadastrado com este " + campo + ".");
            }
            throw new RuntimeException("Erro ao atualizar o cliente.", e);
        }
    }

    public void excluir(Long id) {
        String sql = "UPDATE cliente SET status = false, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
