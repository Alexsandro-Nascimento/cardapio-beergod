package org.example.infrastructure.persistence.postgres;

import org.example.domain.model.Bebida;
import org.example.domain.model.Produto;
import org.example.domain.repository.ProdutoRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostgresProdutoRepository implements ProdutoRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Produto> rowMapper = (rs, rowNum) -> {
        boolean isBebida = rs.getObject("tamanho_ml") != null;

        if (isBebida) {
            return new Bebida(
                    rs.getLong("id"),
                    rs.getString("nome"),
                    rs.getString("descricao"),
                    rs.getBigDecimal("preco"),
                    rs.getString("imagem_url"),
                    rs.getBoolean("ativo"),
                    rs.getLong("categoria_id"),
                    rs.getInt("tamanho_ml"),
                    rs.getString("marca")
            );
        }

        return new Produto(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getString("descricao"),
                rs.getBigDecimal("preco"),
                rs.getString("imagem_url"),
                rs.getBoolean("ativo"),
                rs.getLong("categoria_id")
        );
    };

    public PostgresProdutoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Produto salvar(Produto produto) {
        Integer tamanhoML = (produto instanceof Bebida b) ? b.getTamanhoML() : null;
        String marca = (produto instanceof Bebida b) ? b.getMarca() : null;

        String sql = """
                INSERT INTO produtos (nome, descricao, preco, imagem_url, ativo, categoria_id, tamanho_ml, marca)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id
                """;
        Long id = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getImagemUrl(),
                produto.isAtivo(),
                produto.getCategoriaId(),
                tamanhoML,
                marca
        );

        if (produto instanceof Bebida b) {
            return new Bebida(id, b.getNome(), b.getDescricao(), b.getPreco(), b.getImagemUrl(), b.isAtivo(), b.getCategoriaId(), b.getTamanhoML(), b.getMarca());
        }
        return new Produto(id, produto.getNome(), produto.getDescricao(), produto.getPreco(), produto.getImagemUrl(), produto.isAtivo(), produto.getCategoriaId());
    }

    @Override
    public void atualizar(Produto produto) {
        Integer tamanhoML = (produto instanceof Bebida b) ? b.getTamanhoML() : null;
        String marca = (produto instanceof Bebida b) ? b.getMarca() : null;

        String sql = """
                UPDATE produtos
                SET nome = ?, descricao = ?, preco = ?, imagem_url = ?, ativo = ?, categoria_id = ?, tamanho_ml = ?, marca = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(
                sql,
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getImagemUrl(),
                produto.isAtivo(),
                produto.getCategoriaId(),
                tamanhoML,
                marca,
                produto.getId()
        );
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        String sql = "SELECT id, nome, descricao, preco, imagem_url, ativo, categoria_id, tamanho_ml, marca FROM produtos WHERE id = ?";
        return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
    }

    @Override
    public List<Produto> listarTodos(boolean apenasAtivos) {
        String sql = apenasAtivos
                ? "SELECT id, nome, descricao, preco, imagem_url, ativo, categoria_id, tamanho_ml, marca FROM produtos WHERE ativo = TRUE ORDER BY id"
                : "SELECT id, nome, descricao, preco, imagem_url, ativo, categoria_id, tamanho_ml, marca FROM produtos ORDER BY id";
        return jdbcTemplate.query(sql, rowMapper);
    }
}