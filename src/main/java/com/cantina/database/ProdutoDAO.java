package com.cantina.database;

import com.cantina.entities.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.sql.*;

public class ProdutoDAO {

    public void salvar(Produto produto) {
        String sql = "INSERT INTO produto (nome, quantidade_estoque, descricao, codigo, status, marca_id, unidade_medida_id, valor_compra, valor_venda, quantidade_minima, percentual_lucro, observacao, created_at, updated_at, referencia, categoria_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?)";

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
            statement.setObject(9, produto.getValorVenda());
            statement.setObject(10, produto.getQuantidadeMinima());
            statement.setObject(11, produto.getPercentualLucro());
            statement.setString(12, produto.getObservacao());
            statement.setString(13, produto.getReferencia());
            statement.setObject(14, produto.getCategoriaId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Produto> listarTodos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produto";

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
        String sql = "SELECT * FROM produto WHERE id = ?";
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
        String sql = "UPDATE produto SET nome = ?, quantidade_estoque = ?, descricao = ?, codigo = ?, status = ?, marca_id = ?, unidade_medida_id = ?, valor_compra = ?, valor_venda = ?, quantidade_minima = ?, percentual_lucro = ?, observacao = ?, updated_at = NOW(), referencia = ?, categoria_id = ? WHERE id = ?";

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
            statement.setObject(9, produto.getValorVenda());
            statement.setObject(10, produto.getQuantidadeMinima());
            statement.setObject(11, produto.getPercentualLucro());
            statement.setString(12, produto.getObservacao());
            statement.setString(13, produto.getReferencia());
            statement.setObject(14, produto.getCategoriaId());
            statement.setLong(15, produto.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM produto WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}