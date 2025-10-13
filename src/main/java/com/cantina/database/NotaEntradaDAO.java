package com.cantina.database;

import com.cantina.entities.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class NotaEntradaDAO {

    public void salvar(NotaEntrada notaEntrada) {
        String sql = "INSERT INTO nota_entrada (numero_sequencial, numero, codigo, modelo, serie, codigo_fornecedor, " +
                "fornecedor_id, data_emissao, data_chegada, data_entrega_realizada, condicao_pagamento_id, " +
                "forma_pagamento_id, funcionario_id, status, tipo_frete, transportadora_id, valor_frete, " +
                "valor_seguro, outras_despesas, valor_desconto, valor_acrescimo, total_produtos, total_a_pagar, " +
                "valor_produtos, valor_total, observacoes, ativo, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setObject(1, notaEntrada.getNumeroSequencial());
            statement.setString(2, notaEntrada.getNumero());
            statement.setString(3, notaEntrada.getCodigo());
            statement.setString(4, notaEntrada.getModelo());
            statement.setString(5, notaEntrada.getSerie());
            statement.setString(6, notaEntrada.getCodigoFornecedor());
            statement.setLong(7, notaEntrada.getFornecedorId() != null ? notaEntrada.getFornecedorId() :
                    (notaEntrada.getFornecedor() != null ? notaEntrada.getFornecedor().getId() : null));

            statement.setDate(8, notaEntrada.getDataEmissao() != null ? new java.sql.Date(notaEntrada.getDataEmissao().getTime()) : null);
            statement.setDate(9, notaEntrada.getDataChegada() != null ? new java.sql.Date(notaEntrada.getDataChegada().getTime()) : null);
            statement.setDate(10, notaEntrada.getDataEntregaRealizada() != null ? new java.sql.Date(notaEntrada.getDataEntregaRealizada().getTime()) : null);

            statement.setLong(11, notaEntrada.getCondicaoPagamentoId() != null ? notaEntrada.getCondicaoPagamentoId() :
                    (notaEntrada.getCondicaoPagamento() != null ? notaEntrada.getCondicaoPagamento().getId() : null));
            statement.setObject(12, notaEntrada.getFormaPagamentoId() != null ? notaEntrada.getFormaPagamentoId() :
                    (notaEntrada.getFormaPagamento() != null ? notaEntrada.getFormaPagamento().getId() : null));
            statement.setObject(13, notaEntrada.getFuncionarioId());
            statement.setString(14, notaEntrada.getStatus() != null ? notaEntrada.getStatus() : "PENDENTE");
            statement.setString(15, notaEntrada.getTipoFrete() != null ? notaEntrada.getTipoFrete() : "CIF");
            statement.setObject(16, notaEntrada.getTransportadoraId() != null ? notaEntrada.getTransportadoraId() :
                    (notaEntrada.getTransportadora() != null ? notaEntrada.getTransportadora().getId() : null));

            statement.setBigDecimal(17, notaEntrada.getValorFrete() != null ? notaEntrada.getValorFrete() : BigDecimal.ZERO);
            statement.setBigDecimal(18, notaEntrada.getValorSeguro() != null ? notaEntrada.getValorSeguro() : BigDecimal.ZERO);
            statement.setBigDecimal(19, notaEntrada.getOutrasDespesas() != null ? notaEntrada.getOutrasDespesas() : BigDecimal.ZERO);
            statement.setBigDecimal(20, notaEntrada.getValorDesconto() != null ? notaEntrada.getValorDesconto() : BigDecimal.ZERO);
            statement.setBigDecimal(21, notaEntrada.getValorAcrescimo() != null ? notaEntrada.getValorAcrescimo() : BigDecimal.ZERO);
            statement.setBigDecimal(22, notaEntrada.getTotalProdutos() != null ? notaEntrada.getTotalProdutos() : BigDecimal.ZERO);
            statement.setBigDecimal(23, notaEntrada.getTotalAPagar() != null ? notaEntrada.getTotalAPagar() : BigDecimal.ZERO);
            statement.setBigDecimal(24, notaEntrada.getValorProdutos() != null ? notaEntrada.getValorProdutos() : BigDecimal.ZERO);
            statement.setBigDecimal(25, notaEntrada.getValorTotal() != null ? notaEntrada.getValorTotal() : BigDecimal.ZERO);
            statement.setString(26, notaEntrada.getObservacoes());
            statement.setBoolean(27, notaEntrada.getAtivo() != null ? notaEntrada.getAtivo() : true);

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                notaEntrada.setId(generatedKeys.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar nota de entrada: " + e.getMessage(), e);
        }
    }

    public List<NotaEntrada> listarTodas() {
        List<NotaEntrada> notasEntrada = new ArrayList<>();
        String sql = "SELECT * FROM nota_entrada WHERE ativo = true ORDER BY data_emissao DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                notasEntrada.add(mapearNotaEntrada(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notasEntrada;
    }

    public NotaEntrada buscarPorId(Long id) {
        String sql = "SELECT * FROM nota_entrada WHERE id = ?";
        NotaEntrada notaEntrada = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                notaEntrada = mapearNotaEntrada(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notaEntrada;
    }

    public NotaEntrada buscarPorNumero(String numero) {
        String sql = "SELECT * FROM nota_entrada WHERE numero = ?";
        NotaEntrada notaEntrada = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, numero);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                notaEntrada = mapearNotaEntrada(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notaEntrada;
    }

    public void atualizar(NotaEntrada notaEntrada) {
        String sql = "UPDATE nota_entrada SET numero_sequencial = ?, numero = ?, codigo = ?, modelo = ?, serie = ?, " +
                "codigo_fornecedor = ?, fornecedor_id = ?, data_emissao = ?, data_chegada = ?, data_entrega_realizada = ?, " +
                "condicao_pagamento_id = ?, forma_pagamento_id = ?, funcionario_id = ?, status = ?, tipo_frete = ?, " +
                "transportadora_id = ?, valor_frete = ?, valor_seguro = ?, outras_despesas = ?, valor_desconto = ?, " +
                "valor_acrescimo = ?, total_produtos = ?, total_a_pagar = ?, valor_produtos = ?, valor_total = ?, " +
                "observacoes = ?, ativo = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, notaEntrada.getNumeroSequencial());
            statement.setString(2, notaEntrada.getNumero());
            statement.setString(3, notaEntrada.getCodigo());
            statement.setString(4, notaEntrada.getModelo());
            statement.setString(5, notaEntrada.getSerie());
            statement.setString(6, notaEntrada.getCodigoFornecedor());
            statement.setLong(7, notaEntrada.getFornecedorId() != null ? notaEntrada.getFornecedorId() :
                    (notaEntrada.getFornecedor() != null ? notaEntrada.getFornecedor().getId() : null));

            statement.setDate(8, notaEntrada.getDataEmissao() != null ? new java.sql.Date(notaEntrada.getDataEmissao().getTime()) : null);
            statement.setDate(9, notaEntrada.getDataChegada() != null ? new java.sql.Date(notaEntrada.getDataChegada().getTime()) : null);
            statement.setDate(10, notaEntrada.getDataEntregaRealizada() != null ? new java.sql.Date(notaEntrada.getDataEntregaRealizada().getTime()) : null);

            statement.setLong(11, notaEntrada.getCondicaoPagamentoId() != null ? notaEntrada.getCondicaoPagamentoId() :
                    (notaEntrada.getCondicaoPagamento() != null ? notaEntrada.getCondicaoPagamento().getId() : null));
            statement.setObject(12, notaEntrada.getFormaPagamentoId() != null ? notaEntrada.getFormaPagamentoId() :
                    (notaEntrada.getFormaPagamento() != null ? notaEntrada.getFormaPagamento().getId() : null));
            statement.setObject(13, notaEntrada.getFuncionarioId());
            statement.setString(14, notaEntrada.getStatus() != null ? notaEntrada.getStatus() : "PENDENTE");
            statement.setString(15, notaEntrada.getTipoFrete() != null ? notaEntrada.getTipoFrete() : "CIF");
            statement.setObject(16, notaEntrada.getTransportadoraId() != null ? notaEntrada.getTransportadoraId() :
                    (notaEntrada.getTransportadora() != null ? notaEntrada.getTransportadora().getId() : null));

            statement.setBigDecimal(17, notaEntrada.getValorFrete() != null ? notaEntrada.getValorFrete() : BigDecimal.ZERO);
            statement.setBigDecimal(18, notaEntrada.getValorSeguro() != null ? notaEntrada.getValorSeguro() : BigDecimal.ZERO);
            statement.setBigDecimal(19, notaEntrada.getOutrasDespesas() != null ? notaEntrada.getOutrasDespesas() : BigDecimal.ZERO);
            statement.setBigDecimal(20, notaEntrada.getValorDesconto() != null ? notaEntrada.getValorDesconto() : BigDecimal.ZERO);
            statement.setBigDecimal(21, notaEntrada.getValorAcrescimo() != null ? notaEntrada.getValorAcrescimo() : BigDecimal.ZERO);
            statement.setBigDecimal(22, notaEntrada.getTotalProdutos() != null ? notaEntrada.getTotalProdutos() : BigDecimal.ZERO);
            statement.setBigDecimal(23, notaEntrada.getTotalAPagar() != null ? notaEntrada.getTotalAPagar() : BigDecimal.ZERO);
            statement.setBigDecimal(24, notaEntrada.getValorProdutos() != null ? notaEntrada.getValorProdutos() : BigDecimal.ZERO);
            statement.setBigDecimal(25, notaEntrada.getValorTotal() != null ? notaEntrada.getValorTotal() : BigDecimal.ZERO);
            statement.setString(26, notaEntrada.getObservacoes());
            statement.setBoolean(27, notaEntrada.getAtivo() != null ? notaEntrada.getAtivo() : true);
            statement.setLong(28, notaEntrada.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar nota de entrada: " + e.getMessage(), e);
        }
    }

    public void excluir(Long id) {
        String sql = "UPDATE nota_entrada SET ativo = false, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void atualizarStatus(Long id, String novoStatus) {
        String sql = "UPDATE nota_entrada SET status = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, novoStatus);
            statement.setLong(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private NotaEntrada mapearNotaEntrada(ResultSet resultSet) throws SQLException {
        NotaEntrada notaEntrada = new NotaEntrada();
        notaEntrada.setId(resultSet.getLong("id"));
        notaEntrada.setNumeroSequencial((Integer) resultSet.getObject("numero_sequencial"));
        notaEntrada.setNumero(resultSet.getString("numero"));
        notaEntrada.setCodigo(resultSet.getString("codigo"));
        notaEntrada.setModelo(resultSet.getString("modelo"));
        notaEntrada.setSerie(resultSet.getString("serie"));
        notaEntrada.setCodigoFornecedor(resultSet.getString("codigo_fornecedor"));
        notaEntrada.setFornecedorId(resultSet.getLong("fornecedor_id"));
        notaEntrada.setDataEmissao(resultSet.getDate("data_emissao"));
        notaEntrada.setDataChegada(resultSet.getDate("data_chegada"));
        notaEntrada.setDataEntregaRealizada(resultSet.getDate("data_entrega_realizada"));
        notaEntrada.setCondicaoPagamentoId(resultSet.getLong("condicao_pagamento_id"));
        notaEntrada.setFormaPagamentoId((Long) resultSet.getObject("forma_pagamento_id"));
        notaEntrada.setFuncionarioId((Long) resultSet.getObject("funcionario_id"));
        notaEntrada.setStatus(resultSet.getString("status"));
        notaEntrada.setTipoFrete(resultSet.getString("tipo_frete"));
        notaEntrada.setTransportadoraId((Long) resultSet.getObject("transportadora_id"));
        notaEntrada.setValorFrete(resultSet.getBigDecimal("valor_frete"));
        notaEntrada.setValorSeguro(resultSet.getBigDecimal("valor_seguro"));
        notaEntrada.setOutrasDespesas(resultSet.getBigDecimal("outras_despesas"));
        notaEntrada.setValorDesconto(resultSet.getBigDecimal("valor_desconto"));
        notaEntrada.setValorAcrescimo(resultSet.getBigDecimal("valor_acrescimo"));
        notaEntrada.setTotalProdutos(resultSet.getBigDecimal("total_produtos"));
        notaEntrada.setTotalAPagar(resultSet.getBigDecimal("total_a_pagar"));
        notaEntrada.setValorProdutos(resultSet.getBigDecimal("valor_produtos"));
        notaEntrada.setValorTotal(resultSet.getBigDecimal("valor_total"));
        notaEntrada.setObservacoes(resultSet.getString("observacoes"));
        notaEntrada.setAtivo(resultSet.getBoolean("ativo"));
        notaEntrada.setDataCriacao(resultSet.getDate("created_at"));
        notaEntrada.setUltimaModificacao(resultSet.getDate("updated_at"));

        return notaEntrada;
    }
}
