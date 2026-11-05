package org.example.infrastructure.persistence.postgres;

import org.example.domain.model.Administrador;
import org.example.domain.repository.AdministradorRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PostgresAdministradorRepository implements AdministradorRepository {
    private final DataSource dataSource;
    public PostgresAdministradorRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Administrador salvar(Administrador administrador) {
        String sql = "INSERT INTO administradores (nome, email, senha, ativo) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, administrador.getNome());
            stmt.setString(2, administrador.getEmail());
            stmt.setString(3, administrador.getSenha());
            stmt.setBoolean(4, administrador.isAtivo());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    administrador.setId(rs.getLong("id"));
                }
            }
            return administrador;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar administrador no banco de dados", e);
        }
    }
    @Override
    public Optional<Administrador> buscarPorEmail(String email) {
        String sql = "SELECT * FROM administradores WHERE email = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Administrador admin = new Administrador(
                            rs.getLong("id"),
                            rs.getString("nome"),
                            rs.getString("email"),
                            rs.getString("senha"),
                            rs.getBoolean("ativo")
                    );
                    return Optional.of(admin);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar administrador por e-mail", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Administrador> listarTodos() {
        String sql = "SELECT * FROM administradores";
        List<Administrador> administradores = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Administrador admin = new Administrador(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("senha"),
                        rs.getBoolean("ativo")
                );
                administradores.add(admin);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar administradores", e);
        }
        return administradores;
    }

}
