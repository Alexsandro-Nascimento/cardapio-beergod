package org.example.infrastructure.persistence.postgres;

import org.example.domain.model.Administrador;
import org.example.domain.repository.AdministradorRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostgresAdministradorRepository implements AdministradorRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Administrador> rowMapper = (rs, rowNum) -> new Administrador(
            rs.getLong("id"),
            rs.getString("nome"),
            rs.getString("email"),
            rs.getString("senha"),
            rs.getBoolean("ativo")
    );

    public PostgresAdministradorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvar(Administrador administrador) {
        String sql = "INSERT INTO administradores (nome, email, senha, ativo) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(
                sql,
                administrador.getNome(),
                administrador.getEmail(),
                administrador.getSenha(),
                administrador.isAtivo()
        );
    }

    @Override
    public List<Administrador> listarTodos() {
        String sql = "SELECT id, nome, email, senha, ativo FROM administradores ORDER BY id";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<Administrador> buscarPorEmail(String email) {
        String sql = "SELECT id, nome, email, senha, ativo FROM administradores WHERE email = ?";
        return jdbcTemplate.query(sql, rowMapper, email).stream().findFirst();
    }

    @Override
    public Optional<Administrador> buscarPorId(Long id) {
        String sql = "SELECT id, nome, email, senha, ativo FROM administradores WHERE id = ?";
        return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
    }

    @Override
    public void atualizar(Administrador administrador) {
        String sql = "UPDATE administradores SET nome = ?, email = ?, senha = ?, ativo = ? WHERE id = ?";
        jdbcTemplate.update(
                sql,
                administrador.getNome(),
                administrador.getEmail(),
                administrador.getSenha(),
                administrador.isAtivo(),
                administrador.getId()
        );
    }
}