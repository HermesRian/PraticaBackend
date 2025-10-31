package com.cantina.database;

import com.cantina.entities.ContaPagar;
import com.cantina.entities.FormaPagamento;
import com.cantina.entities.Fornecedor;
import com.cantina.entities.NotaEntrada;
import com.cantina.enums.StatusContaPagar;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class ContaPagarDAO {

    public void salvar(ContaPagar contaPagar) {
        String sql = "INSERT INTO contas_pagar (numero, modelo, serie, parcela, valor, desconto, " +
                "multa, juro, valor_baixa, fornecedor_id, forma_pagamento_id, nota_entrada_id, " +
                "data_vencimento, data_emissao, data_baixa, data_pagamento, data_cancelamento, status, " +
                "descricao, justificativa_cancelamento, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, contaPagar.getNumero());
            statement.setString(2, contaPagar.getModelo());
            statement.setString(3, contaPagar.getSerie());
            statement.setInt(4, contaPagar.getParcela());
            statement.setBigDecimal(5, contaPagar.getValor());
            statement.setBigDecimal(6, contaPagar.getDesconto() != null ? contaPagar.getDesconto() : BigDecimal.ZERO);
            statement.setBigDecimal(7, contaPagar.getMulta() != null ? contaPagar.getMulta() : BigDecimal.ZERO);
            statement.setBigDecimal(8, contaPagar.getJuro() != null ? contaPagar.getJuro() : BigDecimal.ZERO);
            statement.setBigDecimal(9, contaPagar.getValorBaixa());
            statement.setLong(10, contaPagar.getFornecedorId());
            statement.setObject(11, contaPagar.getFormaPagamentoId());
            statement.setObject(12, contaPagar.getNotaEntradaId());
            statement.setDate(13, contaPagar.getDataVencimento() != null ?
                    new java.sql.Date(contaPagar.getDataVencimento().getTime()) : null);
            statement.setDate(14, contaPagar.getDataEmissao() != null ?
                    new java.sql.Date(contaPagar.getDataEmissao().getTime()) : null);
            statement.setDate(15, contaPagar.getDataBaixa() != null ?
                    new java.sql.Date(contaPagar.getDataBaixa().getTime()) : null);
            statement.setDate(16, contaPagar.getDataPagamento() != null ?
                    new java.sql.Date(contaPagar.getDataPagamento().getTime()) : null);
            statement.setDate(17, contaPagar.getDataCancelamento() != null ?
                    new java.sql.Date(contaPagar.getDataCancelamento().getTime()) : null);
            statement.setString(18, contaPagar.getStatus() != null ?
                    contaPagar.getStatus().name() : StatusContaPagar.PENDENTE.name());
            statement.setString(19, contaPagar.getDescricao());
            statement.setString(20, contaPagar.getJustificativaCancelamento());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                contaPagar.setId(generatedKeys.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar conta a pagar: " + e.getMessage(), e);
        }
    }

    public void salvarLista(List<ContaPagar> contas) {
        for (ContaPagar conta : contas) {
            salvar(conta);
        }
    }

    public List<ContaPagar> listarTodas() {
        List<ContaPagar> contas = new ArrayList<>();
        String sql = "SELECT * FROM contas_pagar WHERE data_cancelamento IS NULL ORDER BY data_vencimento ASC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                contas.add(mapearContaPagar(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contas;
    }

    public ContaPagar buscarPorId(Long id) {
        String sql = "SELECT * FROM contas_pagar WHERE id = ?";
        ContaPagar conta = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                conta = mapearContaPagar(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conta;
    }

    public List<ContaPagar> buscarPorNotaEntradaId(Long notaEntradaId) {
        List<ContaPagar> contas = new ArrayList<>();
        String sql = "SELECT * FROM contas_pagar WHERE nota_entrada_id = ? AND data_cancelamento IS NULL ORDER BY parcela ASC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, notaEntradaId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                contas.add(mapearContaPagar(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contas;
    }

    public void atualizar(ContaPagar contaPagar) {
        String sql = "UPDATE contas_pagar SET numero = ?, modelo = ?, serie = ?, parcela = ?, " +
                "valor = ?, desconto = ?, multa = ?, juro = ?, valor_baixa = ?, fornecedor_id = ?, " +
                "forma_pagamento_id = ?, nota_entrada_id = ?, data_vencimento = ?, data_emissao = ?, " +
                "data_baixa = ?, data_pagamento = ?, data_cancelamento = ?, status = ?, descricao = ?, " +
                "justificativa_cancelamento = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, contaPagar.getNumero());
            statement.setString(2, contaPagar.getModelo());
            statement.setString(3, contaPagar.getSerie());
            statement.setInt(4, contaPagar.getParcela());
            statement.setBigDecimal(5, contaPagar.getValor());
            statement.setBigDecimal(6, contaPagar.getDesconto());
            statement.setBigDecimal(7, contaPagar.getMulta());
            statement.setBigDecimal(8, contaPagar.getJuro());
            statement.setBigDecimal(9, contaPagar.getValorBaixa());
            statement.setLong(10, contaPagar.getFornecedorId());
            statement.setObject(11, contaPagar.getFormaPagamentoId());
            statement.setObject(12, contaPagar.getNotaEntradaId());
            statement.setDate(13, contaPagar.getDataVencimento() != null ?
                    new java.sql.Date(contaPagar.getDataVencimento().getTime()) : null);
            statement.setDate(14, contaPagar.getDataEmissao() != null ?
                    new java.sql.Date(contaPagar.getDataEmissao().getTime()) : null);
            statement.setDate(15, contaPagar.getDataBaixa() != null ?
                    new java.sql.Date(contaPagar.getDataBaixa().getTime()) : null);
            statement.setDate(16, contaPagar.getDataPagamento() != null ?
                    new java.sql.Date(contaPagar.getDataPagamento().getTime()) : null);
            statement.setDate(17, contaPagar.getDataCancelamento() != null ?
                    new java.sql.Date(contaPagar.getDataCancelamento().getTime()) : null);
            statement.setString(18, contaPagar.getStatus() != null ? contaPagar.getStatus().name() : StatusContaPagar.PENDENTE.name());
            statement.setString(19, contaPagar.getDescricao());
            statement.setString(20, contaPagar.getJustificativaCancelamento());
            statement.setLong(21, contaPagar.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar conta a pagar: " + e.getMessage(), e);
        }
    }

    public void marcarComoPaga(Long id, Date dataPagamento) {
        String sql = "UPDATE contas_pagar SET data_pagamento = ?, status = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, dataPagamento != null ? new java.sql.Date(dataPagamento.getTime()) : null);
            statement.setString(2, StatusContaPagar.PAGA.name());
            statement.setLong(3, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao marcar conta como paga: " + e.getMessage(), e);
        }
    }

    public void cancelar(Long id, Date dataCancelamento, String justificativa) {
        String sql = "UPDATE contas_pagar SET data_cancelamento = ?, justificativa_cancelamento = ?, status = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, dataCancelamento != null ? new java.sql.Date(dataCancelamento.getTime()) : new java.sql.Date(new java.util.Date().getTime()));
            statement.setString(2, justificativa);
            statement.setString(3, StatusContaPagar.CANCELADA.name());
            statement.setLong(4, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao cancelar conta a pagar: " + e.getMessage(), e);
        }
    }

    public int contarPagasPorNota(Long notaEntradaId) {
        String sql = "SELECT COUNT(*) FROM contas_pagar WHERE nota_entrada_id = ? AND data_pagamento IS NOT NULL AND data_cancelamento IS NULL";
        int count = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, notaEntradaId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public boolean verificarPrimeiraContaPagaDaNota(Long notaEntradaId) {
        return contarPagasPorNota(notaEntradaId) == 0;
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM contas_pagar WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao excluir conta a pagar: " + e.getMessage(), e);
        }
    }

    public void cancelarTodasPorNotaEntrada(Long notaEntradaId, String justificativa) {
        String sql = "UPDATE contas_pagar SET data_cancelamento = NOW(), justificativa_cancelamento = ?, status = ?, updated_at = NOW() " +
                "WHERE nota_entrada_id = ? AND data_pagamento IS NULL AND data_cancelamento IS NULL";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, justificativa);
            statement.setString(2, StatusContaPagar.CANCELADA.name());
            statement.setLong(3, notaEntradaId);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao cancelar contas a pagar: " + e.getMessage(), e);
        }
    }

    private ContaPagar mapearContaPagar(ResultSet resultSet) throws SQLException {
        ContaPagar conta = new ContaPagar();
        conta.setId(resultSet.getLong("id"));
        conta.setNumero(resultSet.getString("numero"));
        conta.setModelo(resultSet.getString("modelo"));
        conta.setSerie(resultSet.getString("serie"));
        conta.setParcela(resultSet.getInt("parcela"));
        conta.setValor(resultSet.getBigDecimal("valor"));
        conta.setDesconto(resultSet.getBigDecimal("desconto"));
        conta.setMulta(resultSet.getBigDecimal("multa"));
        conta.setJuro(resultSet.getBigDecimal("juro"));
        conta.setValorBaixa(resultSet.getBigDecimal("valor_baixa"));
        conta.setFornecedorId(resultSet.getLong("fornecedor_id"));
        conta.setFormaPagamentoId((Long) resultSet.getObject("forma_pagamento_id"));
        conta.setNotaEntradaId((Long) resultSet.getObject("nota_entrada_id"));
        conta.setDataVencimento(resultSet.getDate("data_vencimento"));
        conta.setDataEmissao(resultSet.getDate("data_emissao"));
        conta.setDataBaixa(resultSet.getDate("data_baixa"));
        conta.setDataPagamento(resultSet.getDate("data_pagamento"));
        conta.setDataCancelamento(resultSet.getDate("data_cancelamento"));
        conta.setStatus(StatusContaPagar.valueOf(resultSet.getString("status")));
        conta.setDescricao(resultSet.getString("descricao"));
        conta.setJustificativaCancelamento(resultSet.getString("justificativa_cancelamento"));
        conta.setDataCriacao(resultSet.getTimestamp("created_at"));
        conta.setUltimaModificacao(resultSet.getTimestamp("updated_at"));

        if (conta.getFornecedorId() != null) {
            FornecedorDAO fornecedorDAO = new FornecedorDAO();
            conta.setFornecedor(fornecedorDAO.buscarPorId(conta.getFornecedorId()));
        }

        if (conta.getFormaPagamentoId() != null) {
            FormaPagamentoDAO formaPagamentoDAO = new FormaPagamentoDAO();
            conta.setFormaPagamento(formaPagamentoDAO.buscarPorId(conta.getFormaPagamentoId()));
        }

        if (conta.getNotaEntradaId() != null) {
            NotaEntradaDAO notaEntradaDAO = new NotaEntradaDAO();
            conta.setNotaEntrada(notaEntradaDAO.buscarPorId(conta.getNotaEntradaId()));
        }

        return conta;
    }
}