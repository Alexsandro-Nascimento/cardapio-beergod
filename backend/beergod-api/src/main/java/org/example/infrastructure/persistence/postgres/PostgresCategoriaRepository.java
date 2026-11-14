package org.example.infrastructure.persistence.postgres;

import org.example.domain.model.Categoria;
import org.example.domain.repository.CategoriaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostgresCategoriaRepository implements CategoriaRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Categoria> rowMapper = (rs, rowNum) -> new Categoria(
            rs.getLong("id"),
            rs.getString("nome"),
            rs.getBoolean("ativo")
    );

    public PostgresCategoriaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Categoria salvar(Categoria categoria) {
        String sql = "INSERT INTO categorias (nome, ativo) VALUES (?, ?) RETURNING id";
        Long id = jdbcTemplate.queryForObject(sql, Long.class, categoria.getNome(), categoria.isAtivo());
        return new Categoria(id, categoria.getNome(), categoria.isAtivo());
    }

    @Override
    public void atualizar(Categoria categoria) {
        String sql = "UPDATE categorias SET nome = ?, ativo = ? WHERE id = ?";
        jdbcTemplate.update(sql, categoria.getNome(), categoria.isAtivo(), categoria.getId());
    }

    @Override
    public Optional<Categoria> buscarPorId(Long id) {
        String sql = "SELECT id, nome, ativo FROM categorias WHERE id = ?";
        return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
    }

    @Override
    public List<Categoria> listarTodos(boolean apenasAtivas) {
        String sql = apenasAtivas
                ? "SELECT id, nome, ativo FROM categorias WHERE ativo = TRUE ORDER BY id"
                : "SELECT id, nome, ativo FROM categorias ORDER BY id";
        return jdbcTemplate.query(sql, rowMapper);
    }
}