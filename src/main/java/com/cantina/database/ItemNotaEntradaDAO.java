package com.cantina.database;

import com.cantina.entities.ItemNotaEntrada;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemNotaEntradaDAO {

    public void salvar(ItemNotaEntrada item) {
        calcularValorTotalItem(item);
        String sql = "INSERT INTO produtos_nota (nota_entrada_id, produto_id, sequencia, quantidade, " +
                "valor_unitario, valor_desconto, percentual_desconto, valor_total, rateio_frete, " +
                "rateio_seguro, rateio_outras, custo_preco_final, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, item.getNotaEntradaId());
            statement.setLong(2, item.getProdutoId());
            statement.setInt(3, item.getSequencia() != null ? item.getSequencia() : 1);
            statement.setBigDecimal(4, item.getQuantidade());
            statement.setBigDecimal(5, item.getValorUnitario());
            statement.setBigDecimal(6, item.getValorDesconto() != null ? item.getValorDesconto() : BigDecimal.ZERO);
            statement.setBigDecimal(7, item.getPercentualDesconto() != null ? item.getPercentualDesconto() : BigDecimal.ZERO);
            statement.setBigDecimal(8, item.getValorTotal());
            statement.setBigDecimal(9, item.getRateioFrete() != null ? item.getRateioFrete() : BigDecimal.ZERO);
            statement.setBigDecimal(10, item.getRateioSeguro() != null ? item.getRateioSeguro() : BigDecimal.ZERO);
            statement.setBigDecimal(11, item.getRateioOutras() != null ? item.getRateioOutras() : BigDecimal.ZERO);
            statement.setObject(12, item.getCustoPrecoFinal());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar item da nota de entrada: " + e.getMessage(), e);
        }
    }

    public void salvarLista(Long notaEntradaId, List<ItemNotaEntrada> itens) {
        if (itens == null || itens.isEmpty()) {
            return;
        }

        for (int i = 0; i < itens.size(); i++) {
            ItemNotaEntrada item = itens.get(i);
            item.setNotaEntradaId(notaEntradaId);
            item.setSequencia(i + 1);
            salvar(item);
        }
    }

    public List<ItemNotaEntrada> buscarPorNotaEntrada(Long notaEntradaId) {
        List<ItemNotaEntrada> itens = new ArrayList<>();
        String sql = "SELECT * FROM produtos_nota WHERE nota_entrada_id = ? ORDER BY sequencia";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, notaEntradaId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                itens.add(mapearItem(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return itens;
    }

    public void atualizar(ItemNotaEntrada item) {
        String sql = "UPDATE produtos_nota SET quantidade = ?, valor_unitario = ?, valor_desconto = ?, " +
                "percentual_desconto = ?, valor_total = ?, rateio_frete = ?, rateio_seguro = ?, " +
                "rateio_outras = ?, custo_preco_final = ?, updated_at = NOW() " +
                "WHERE nota_entrada_id = ? AND produto_id = ? AND sequencia = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, item.getQuantidade());
            statement.setBigDecimal(2, item.getValorUnitario());
            statement.setBigDecimal(3, item.getValorDesconto() != null ? item.getValorDesconto() : BigDecimal.ZERO);
            statement.setBigDecimal(4, item.getPercentualDesconto() != null ? item.getPercentualDesconto() : BigDecimal.ZERO);
            statement.setBigDecimal(5, item.getValorTotal());
            statement.setBigDecimal(6, item.getRateioFrete() != null ? item.getRateioFrete() : BigDecimal.ZERO);
            statement.setBigDecimal(7, item.getRateioSeguro() != null ? item.getRateioSeguro() : BigDecimal.ZERO);
            statement.setBigDecimal(8, item.getRateioOutras() != null ? item.getRateioOutras() : BigDecimal.ZERO);
            statement.setObject(9, item.getCustoPrecoFinal());
            statement.setLong(10, item.getNotaEntradaId());
            statement.setLong(11, item.getProdutoId());
            statement.setInt(12, item.getSequencia());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar item da nota de entrada: " + e.getMessage(), e);
        }
    }

    public void excluirPorNotaEntrada(Long notaEntradaId) {
        String sql = "DELETE FROM produtos_nota WHERE nota_entrada_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, notaEntradaId);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluirItem(Long notaEntradaId, Long produtoId, Integer sequencia) {
        String sql = "DELETE FROM produtos_nota WHERE nota_entrada_id = ? AND produto_id = ? AND sequencia = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, notaEntradaId);
            statement.setLong(2, produtoId);
            statement.setInt(3, sequencia);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ItemNotaEntrada mapearItem(ResultSet resultSet) throws SQLException {
        ItemNotaEntrada item = new ItemNotaEntrada();
        item.setNotaEntradaId(resultSet.getLong("nota_entrada_id"));
        item.setProdutoId(resultSet.getLong("produto_id"));
        item.setSequencia(resultSet.getInt("sequencia"));
        item.setQuantidade(resultSet.getBigDecimal("quantidade"));
        item.setValorUnitario(resultSet.getBigDecimal("valor_unitario"));
        item.setValorDesconto(resultSet.getBigDecimal("valor_desconto"));
        item.setPercentualDesconto(resultSet.getBigDecimal("percentual_desconto"));
        item.setValorTotal(resultSet.getBigDecimal("valor_total"));
        item.setRateioFrete(resultSet.getBigDecimal("rateio_frete"));
        item.setRateioSeguro(resultSet.getBigDecimal("rateio_seguro"));
        item.setRateioOutras(resultSet.getBigDecimal("rateio_outras"));
        item.setCustoPrecoFinal(resultSet.getBigDecimal("custo_preco_final"));
        item.setDataCriacao(resultSet.getTimestamp("created_at"));
        item.setUltimaModificacao(resultSet.getTimestamp("updated_at"));
        return item;
    }

    private void calcularValorTotalItem(ItemNotaEntrada item) {
        if (item.getValorTotal() == null || item.getValorTotal().compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal quantidade = item.getQuantidade() != null ? item.getQuantidade() : BigDecimal.ZERO;
            BigDecimal valorUnitario = item.getValorUnitario() != null ? item.getValorUnitario() : BigDecimal.ZERO;
            BigDecimal valorDesconto = item.getValorDesconto() != null ? item.getValorDesconto() : BigDecimal.ZERO;

            BigDecimal valorTotal = quantidade.multiply(valorUnitario).subtract(valorDesconto);
            item.setValorTotal(valorTotal);
        }
    }

}
