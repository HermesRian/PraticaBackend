package com.cantina.database;

import com.cantina.entities.Produto;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;

@Repository
public class ProdutoDAO {

    public void salvar(Produto produto) {
        String sql = "INSERT INTO produtos (nome, quantidade_estoque, descricao, codigo, status, marca_id, unidade_medida_id, valor_compra, custo_produto, valor_venda, quantidade_minima, percentual_lucro, observacao, created_at, updated_at, referencia, categoria_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, produto.getNome());
            statement.setObject(2, produto.getQuantidadeEstoque());
            statement.setString(3, produto.getDescricao());
            statement.setString(4, produto.getCodigo());
            statement.setBoolean(5, produto.getAtivo() != null ? produto.getAtivo() : true);
            statement.setObject(6, produto.getMarcaId());
            statement.setObject(7, produto.getUnidadeMedidaId());
            statement.setObject(8, produto.getValorCompra());
            statement.setObject(9, produto.getCustoProduto());
            statement.setObject(10, produto.getValorVenda());
            statement.setObject(11, produto.getQuantidadeMinima());
            statement.setObject(12, produto.getPercentualLucro());
            statement.setString(13, produto.getObservacao());
            statement.setString(14, produto.getReferencia());
            statement.setObject(15, produto.getCategoriaId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Produto> listarTodos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Produto produto = new Produto();
                produto.setId(resultSet.getLong("id"));
                produto.setNome(resultSet.getString("nome"));
                produto.setReferencia(resultSet.getString("referencia"));
                produto.setCategoriaId((Integer) resultSet.getObject("categoria_id"));
                produto.setQuantidadeEstoque((Integer) resultSet.getObject("quantidade_estoque"));
                produto.setDescricao(resultSet.getString("descricao"));
                produto.setCodigo(resultSet.getString("codigo"));
                produto.setAtivo(resultSet.getBoolean("status"));
                produto.setMarcaId((Long) resultSet.getObject("marca_id"));
                produto.setUnidadeMedidaId((Long) resultSet.getObject("unidade_medida_id"));
                produto.setValorCompra(resultSet.getBigDecimal("valor_compra"));
                produto.setCustoProduto(resultSet.getBigDecimal("custo_produto"));
                produto.setValorVenda(resultSet.getBigDecimal("valor_venda"));
                produto.setQuantidadeMinima((Integer) resultSet.getObject("quantidade_minima"));
                produto.setPercentualLucro(resultSet.getBigDecimal("percentual_lucro"));
                produto.setObservacao(resultSet.getString("observacao"));
                produto.setDataCriacao(resultSet.getDate("created_at"));
                produto.setUltimaModificacao(resultSet.getDate("updated_at"));

                produtos.add(produto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return produtos;
    }

    public Produto buscarPorId(Long id) {
        String sql = "SELECT * FROM produtos WHERE id = ?";
        Produto produto = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                produto = new Produto();
                produto.setId(resultSet.getLong("id"));
                produto.setNome(resultSet.getString("nome"));
                produto.setReferencia(resultSet.getString("referencia"));
                produto.setCategoriaId((Integer) resultSet.getObject("categoria_id"));
                produto.setQuantidadeEstoque((Integer) resultSet.getObject("quantidade_estoque"));
                produto.setDescricao(resultSet.getString("descricao"));
                produto.setCodigo(resultSet.getString("codigo"));
                produto.setAtivo(resultSet.getBoolean("status"));
                produto.setMarcaId((Long) resultSet.getObject("marca_id"));
                produto.setUnidadeMedidaId((Long) resultSet.getObject("unidade_medida_id"));
                produto.setValorCompra(resultSet.getBigDecimal("valor_compra"));
                produto.setCustoProduto(resultSet.getBigDecimal("custo_produto"));
                produto.setValorVenda(resultSet.getBigDecimal("valor_venda"));
                produto.setQuantidadeMinima((Integer) resultSet.getObject("quantidade_minima"));
                produto.setPercentualLucro(resultSet.getBigDecimal("percentual_lucro"));
                produto.setObservacao(resultSet.getString("observacao"));
                produto.setDataCriacao(resultSet.getDate("created_at"));
                produto.setUltimaModificacao(resultSet.getDate("updated_at"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return produto;
    }

    public void atualizar(Produto produto) {
        String sql = "UPDATE produtos SET nome = ?, quantidade_estoque = ?, descricao = ?, codigo = ?, status = ?, marca_id = ?, unidade_medida_id = ?, valor_compra = ?, custo_produto = ?, valor_venda = ?, quantidade_minima = ?, percentual_lucro = ?, observacao = ?, updated_at = NOW(), referencia = ?, categoria_id = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, produto.getNome());
            statement.setObject(2, produto.getQuantidadeEstoque());
            statement.setString(3, produto.getDescricao());
            statement.setString(4, produto.getCodigo());
            statement.setBoolean(5, produto.getAtivo() != null ? produto.getAtivo() : true);
            statement.setObject(6, produto.getMarcaId());
            statement.setObject(7, produto.getUnidadeMedidaId());
            statement.setObject(8, produto.getValorCompra());
            statement.setObject(9, produto.getCustoProduto());
            statement.setObject(10, produto.getValorVenda());
            statement.setObject(11, produto.getQuantidadeMinima());
            statement.setObject(12, produto.getPercentualLucro());
            statement.setString(13, produto.getObservacao());
            statement.setString(14, produto.getReferencia());
            statement.setObject(15, produto.getCategoriaId());
            statement.setLong(16, produto.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM produtos WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void atualizarEstoque(Long id, Integer novaQuantidade) {
        String sql = "UPDATE produtos SET quantidade_estoque = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, novaQuantidade);
            statement.setLong(2, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar estoque do produto: " + e.getMessage(), e);
        }
    }

    public void atualizarValorCompra(Long id, BigDecimal valorCompra) {
        String sql = "UPDATE produtos SET valor_compra = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, valorCompra);
            statement.setLong(2, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar valor de compra do produto: " + e.getMessage(), e);
        }
    }

    public void atualizarValorVenda(Long id, BigDecimal valorVenda) {
        String sql = "UPDATE produtos SET valor_venda = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, valorVenda);
            statement.setLong(2, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar valor de venda do produto: " + e.getMessage(), e);
        }
    }

    public void atualizarCustoProduto(Long id, BigDecimal custoProduto) {
        String sql = "UPDATE produtos SET custo_produto = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, custoProduto);
            statement.setLong(2, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar custo do produto: " + e.getMessage(), e);
        }
    }

    public void calcularEAtualizarPercentualLucro(Long id) {
        Produto produto = buscarPorId(id);

        if (produto == null) {
            return;
        }

        BigDecimal custoProduto = produto.getCustoProduto();
        BigDecimal valorVenda = produto.getValorVenda();

        if (custoProduto == null || valorVenda == null ||
            custoProduto.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        BigDecimal lucro = valorVenda.subtract(custoProduto);
        BigDecimal percentualLucro = lucro
            .divide(custoProduto, 6, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .setScale(2, RoundingMode.HALF_UP);

        String sql = "UPDATE produtos SET percentual_lucro = ?, updated_at = NOW() WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, percentualLucro);
            statement.setLong(2, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar percentual de lucro do produto: " + e.getMessage(), e);
        }
    }
}