package com.cantina.database;

import com.cantina.entities.ContaReceber;
import com.cantina.entities.FormaPagamento;
import com.cantina.entities.Cliente;
import com.cantina.entities.NotaSaida;
import com.cantina.enums.StatusContaReceber;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class ContaReceberDAO {

    public void salvar(ContaReceber contaReceber) {
        String sql = "INSERT INTO contas_receber (numero, modelo, serie, parcela, valor, desconto, " +
                "multa, juro, valor_baixa, cliente_id, forma_pagamento_id, nota_saida_id, " +
                "data_vencimento, data_emissao, data_baixa, data_pagamento, data_cancelamento, status, " +
                "descricao, justificativa_cancelamento, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, contaReceber.getNumero());
            statement.setString(2, contaReceber.getModelo());
            statement.setString(3, contaReceber.getSerie());
            statement.setInt(4, contaReceber.getParcela());
            statement.setBigDecimal(5, contaReceber.getValor());
            statement.setBigDecimal(6, contaReceber.getDesconto() != null ? contaReceber.getDesconto() : BigDecimal.ZERO);
            statement.setBigDecimal(7, contaReceber.getMulta() != null ? contaReceber.getMulta() : BigDecimal.ZERO);
            statement.setBigDecimal(8, contaReceber.getJuro() != null ? contaReceber.getJuro() : BigDecimal.ZERO);
            statement.setBigDecimal(9, contaReceber.getValorBaixa());
            statement.setLong(10, contaReceber.getClienteId());
            statement.setObject(11, contaReceber.getFormaPagamentoId());
            statement.setObject(12, contaReceber.getNotaSaidaId());
            statement.setDate(13, contaReceber.getDataVencimento() != null ?
                    new java.sql.Date(contaReceber.getDataVencimento().getTime()) : null);
            statement.setDate(14, contaReceber.getDataEmissao() != null ?
                    new java.sql.Date(contaReceber.getDataEmissao().getTime()) : null);
            statement.setDate(15, contaReceber.getDataBaixa() != null ?
                    new java.sql.Date(contaReceber.getDataBaixa().getTime()) : null);
            statement.setDate(16, contaReceber.getDataPagamento() != null ?
                    new java.sql.Date(contaReceber.getDataPagamento().getTime()) : null);
            statement.setDate(17, contaReceber.getDataCancelamento() != null ?
                    new java.sql.Date(contaReceber.getDataCancelamento().getTime()) : null);
            statement.setString(18, contaReceber.getStatus() != null ?
                    contaReceber.getStatus().name() : StatusContaReceber.PENDENTE.name());
            statement.setString(19, contaReceber.getDescricao());
            statement.setString(20, contaReceber.getJustificativaCancelamento());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                contaReceber.setId(generatedKeys.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar conta a receber: " + e.getMessage(), e);
        }
    }

    public void salvarLista(List<ContaReceber> contas) {
        for (ContaReceber conta : contas) {
            salvar(conta);
        }
    }

    public List<ContaReceber> listarTodas() {
        List<ContaReceber> contas = new ArrayList<>();
        String sql = "SELECT * FROM contas_receber ORDER BY data_vencimento ASC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                contas.add(mapearContaReceber(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contas;
    }

    public ContaReceber buscarPorId(Long id) {
        String sql = "SELECT * FROM contas_receber WHERE id = ?";
        ContaReceber conta = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                conta = mapearContaReceber(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conta;
    }

    public List<ContaReceber> buscarPorNotaSaidaId(Long notaSaidaId) {
        List<ContaReceber> contas = new ArrayList<>();
        String sql = "SELECT * FROM contas_receber WHERE nota_saida_id = ? AND data_cancelamento IS NULL ORDER BY parcela ASC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, notaSaidaId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                contas.add(mapearContaReceber(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contas;
    }

    public void atualizar(ContaReceber contaReceber) {
        String sql = "UPDATE contas_receber SET numero = ?, modelo = ?, serie = ?, parcela = ?, " +
                "valor = ?, desconto = ?, multa = ?, juro = ?, valor_baixa = ?, cliente_id = ?, " +
                "forma_pagamento_id = ?, nota_saida_id = ?, data_vencimento = ?, data_emissao = ?, " +
                "data_baixa = ?, data_pagamento = ?, data_cancelamento = ?, status = ?, descricao = ?, " +
                "justificativa_cancelamento = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, contaReceber.getNumero());
            statement.setString(2, contaReceber.getModelo());
            statement.setString(3, contaReceber.getSerie());
            statement.setInt(4, contaReceber.getParcela());
            statement.setBigDecimal(5, contaReceber.getValor());
            statement.setBigDecimal(6, contaReceber.getDesconto());
            statement.setBigDecimal(7, contaReceber.getMulta());
            statement.setBigDecimal(8, contaReceber.getJuro());
            statement.setBigDecimal(9, contaReceber.getValorBaixa());
            statement.setLong(10, contaReceber.getClienteId());
            statement.setObject(11, contaReceber.getFormaPagamentoId());
            statement.setObject(12, contaReceber.getNotaSaidaId());
            statement.setDate(13, contaReceber.getDataVencimento() != null ?
                    new java.sql.Date(contaReceber.getDataVencimento().getTime()) : null);
            statement.setDate(14, contaReceber.getDataEmissao() != null ?
                    new java.sql.Date(contaReceber.getDataEmissao().getTime()) : null);
            statement.setDate(15, contaReceber.getDataBaixa() != null ?
                    new java.sql.Date(contaReceber.getDataBaixa().getTime()) : null);
            statement.setDate(16, contaReceber.getDataPagamento() != null ?
                    new java.sql.Date(contaReceber.getDataPagamento().getTime()) : null);
            statement.setDate(17, contaReceber.getDataCancelamento() != null ?
                    new java.sql.Date(contaReceber.getDataCancelamento().getTime()) : null);
            statement.setString(18, contaReceber.getStatus() != null ? contaReceber.getStatus().name() : StatusContaReceber.PENDENTE.name());
            statement.setString(19, contaReceber.getDescricao());
            statement.setString(20, contaReceber.getJustificativaCancelamento());
            statement.setLong(21, contaReceber.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar conta a receber: " + e.getMessage(), e);
        }
    }

    public void marcarComoPaga(Long id, Date dataPagamento) {
        String sql = "UPDATE contas_receber SET data_pagamento = ?, status = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, dataPagamento != null ? new java.sql.Date(dataPagamento.getTime()) : null);
            statement.setString(2, StatusContaReceber.PAGA.name());
            statement.setLong(3, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao marcar conta como paga: " + e.getMessage(), e);
        }
    }

    public void cancelar(Long id, Date dataCancelamento, String justificativa) {
        String sql = "UPDATE contas_receber SET data_cancelamento = ?, justificativa_cancelamento = ?, status = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, dataCancelamento != null ? new java.sql.Date(dataCancelamento.getTime()) : new java.sql.Date(new java.util.Date().getTime()));
            statement.setString(2, justificativa);
            statement.setString(3, StatusContaReceber.CANCELADA.name());
            statement.setLong(4, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao cancelar conta a receber: " + e.getMessage(), e);
        }
    }

    public int contarPagasPorNota(Long notaSaidaId) {
        String sql = "SELECT COUNT(*) FROM contas_receber WHERE nota_saida_id = ? AND data_pagamento IS NOT NULL AND data_cancelamento IS NULL";
        int count = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, notaSaidaId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public boolean verificarPrimeiraContaPagaDaNota(Long notaSaidaId) {
        return contarPagasPorNota(notaSaidaId) == 0;
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM contas_receber WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao excluir conta a receber: " + e.getMessage(), e);
        }
    }

    public void cancelarTodasPorNotaSaida(Long notaSaidaId, String justificativa) {
        String sql = "UPDATE contas_receber SET data_cancelamento = NOW(), justificativa_cancelamento = ?, status = ?, updated_at = NOW() " +
                "WHERE nota_saida_id = ? AND data_pagamento IS NULL AND data_cancelamento IS NULL";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, justificativa);
            statement.setString(2, StatusContaReceber.CANCELADA.name());
            statement.setLong(3, notaSaidaId);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao cancelar contas a receber: " + e.getMessage(), e);
        }
    }

    private ContaReceber mapearContaReceber(ResultSet resultSet) throws SQLException {
        ContaReceber conta = new ContaReceber();
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
        conta.setClienteId(resultSet.getLong("cliente_id"));
        conta.setFormaPagamentoId((Long) resultSet.getObject("forma_pagamento_id"));
        conta.setNotaSaidaId((Long) resultSet.getObject("nota_saida_id"));
        conta.setDataVencimento(resultSet.getDate("data_vencimento"));
        conta.setDataEmissao(resultSet.getDate("data_emissao"));
        conta.setDataBaixa(resultSet.getDate("data_baixa"));
        conta.setDataPagamento(resultSet.getDate("data_pagamento"));
        conta.setDataCancelamento(resultSet.getDate("data_cancelamento"));
        conta.setStatus(StatusContaReceber.valueOf(resultSet.getString("status")));
        conta.setDescricao(resultSet.getString("descricao"));
        conta.setJustificativaCancelamento(resultSet.getString("justificativa_cancelamento"));
        conta.setDataCriacao(resultSet.getTimestamp("created_at"));
        conta.setUltimaModificacao(resultSet.getTimestamp("updated_at"));

        if (conta.getClienteId() != null) {
            ClienteDAO clienteDAO = new ClienteDAO();
            conta.setCliente(clienteDAO.buscarPorId(conta.getClienteId()));
        }

        if (conta.getFormaPagamentoId() != null) {
            FormaPagamentoDAO formaPagamentoDAO = new FormaPagamentoDAO();
            conta.setFormaPagamento(formaPagamentoDAO.buscarPorId(conta.getFormaPagamentoId()));
        }

        if (conta.getNotaSaidaId() != null) {
            NotaSaidaDAO notaSaidaDAO = new NotaSaidaDAO();
            conta.setNotaSaida(notaSaidaDAO.buscarPorId(conta.getNotaSaidaId()));
        }

        return conta;
    }
}
