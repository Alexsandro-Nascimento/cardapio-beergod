package org.example.infrastructure.persistence.postgres;

import org.example.domain.model.Bebida;
import org.example.domain.model.Produto;
import org.example.domain.repository.ProdutoRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PostgresProdutoRepository implements ProdutoRepository {

    @Override
    public Produto salvar(Produto produto) {
        String sql = "INSERT INTO produtos (nome, descricao, preco, categoria, tamanho_ml, marca) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setDouble(3, produto.getPreco());
            stmt.setString(4, produto.getCategoria().name());

            if (produto instanceof Bebida bebida) {
                stmt.setInt(5, bebida.getTamanhoML());
                stmt.setString(6, bebida.getMarca());
            } else {
                stmt.setNull(5, Types.INTEGER);
                stmt.setNull(6, Types.VARCHAR);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Long generatedId = rs.getLong("id");

                if (produto instanceof Bebida bebida) {
                    return new Bebida(
                            generatedId,
                            bebida.getNome(),
                            bebida.getDescricao(),
                            bebida.getPreco(),
                            bebida.getTamanhoML(),
                            bebida.getMarca()
                    );
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar produto no PostgreSQL", e);
        }
        return produto;
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        String sql = "SELECT * FROM produtos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToProduto(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto por ID no PostgreSQL", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Produto> buscarTodos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                produtos.add(mapResultSetToProduto(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todos os produtos no PostgreSQL", e);
        }

        return produtos;
    }

    @Override
    public void deletar(Long id) {
        String sql = "DELETE FROM produtos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar produto no PostgreSQL", e);
        }
    }

    private Produto mapResultSetToProduto(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String nome = rs.getString("nome");
        String descricao = rs.getString("descricao");
        double preco = rs.getDouble("preco");
        int tamanhoML = rs.getInt("tamanho_ml");
        String marca = rs.getString("marca");


        if (marca != null && !marca.isBlank()) {
            return new Bebida(id, nome, descricao, preco, tamanhoML, marca);
        }

        return new Bebida(id, nome, descricao, preco, tamanhoML, marca);
    }
}