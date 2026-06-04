package br.com.gwfrete.dao;

import br.com.gwfrete.model.CategoriaCnh;
import br.com.gwfrete.model.Motorista;
import br.com.gwfrete.model.StatusMotorista;
import br.com.gwfrete.model.TipoVinculoMotorista;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MotoristaDAO {

    private static final String COLUNAS =
            "id, nome, cpf, data_nascimento, telefone, " +
            "cnh_numero, cnh_categoria, cnh_validade, tipo_vinculo, status";

    public void inserir(Motorista motorista, Connection conn) throws SQLException {
        String sql = "INSERT INTO motorista (nome, cpf, data_nascimento, telefone, " +
                "cnh_numero, cnh_categoria, cnh_validade, tipo_vinculo, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, motorista.getNome());
            stmt.setString(2, motorista.getCpf());
            stmt.setDate(3, Date.valueOf(motorista.getDataNascimento()));
            stmt.setString(4, motorista.getTelefone());
            stmt.setString(5, motorista.getNumeroCnh());
            stmt.setString(6, motorista.getCategoriaCnh().name());
            stmt.setDate(7, Date.valueOf(motorista.getDataValidadeCnh()));
            stmt.setString(8, motorista.getTipoVinculo().name());
            stmt.setString(9, motorista.getStatus().name());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    motorista.setId(rs.getLong(1));
                }
            }
        }
    }

    public void atualizar(Motorista motorista, Connection conn) throws SQLException {
        String sql = "UPDATE motorista SET nome = ?, cpf = ?, data_nascimento = ?, telefone = ?, " +
                "cnh_numero = ?, cnh_categoria = ?, cnh_validade = ?, tipo_vinculo = ?, status = ? " +
                "WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, motorista.getNome());
            stmt.setString(2, motorista.getCpf());
            stmt.setDate(3, Date.valueOf(motorista.getDataNascimento()));
            stmt.setString(4, motorista.getTelefone());
            stmt.setString(5, motorista.getNumeroCnh());
            stmt.setString(6, motorista.getCategoriaCnh().name());
            stmt.setDate(7, Date.valueOf(motorista.getDataValidadeCnh()));
            stmt.setString(8, motorista.getTipoVinculo().name());
            stmt.setString(9, motorista.getStatus().name());
            stmt.setLong(10, motorista.getId());

            stmt.executeUpdate();
        }
    }

    public Motorista buscarPorId(Long id, Connection conn) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM motorista WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public Motorista buscarPorCpf(String cpf, Connection conn) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM motorista WHERE cpf = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public List<Motorista> listar(String filtro, int pagina, int itensPorPagina, Connection conn) throws SQLException {
        List<Motorista> motoristas = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT ").append(COLUNAS).append(" FROM motorista WHERE 1=1 ");

        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append("AND (UPPER(nome) LIKE UPPER(?) OR cpf LIKE ?) ");
        }

        sql.append("ORDER BY nome ASC ");
        sql.append("LIMIT ? OFFSET ?");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;

            if (filtro != null && !filtro.trim().isEmpty()) {
                String like = "%" + filtro.trim() + "%";
                stmt.setString(idx++, like);
                stmt.setString(idx++, like);
            }

            stmt.setInt(idx++, itensPorPagina);
            stmt.setInt(idx, (pagina - 1) * itensPorPagina);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    motoristas.add(mapear(rs));
                }
            }
        }
        return motoristas;
    }

    public int contarTotal(String filtro, Connection conn) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM motorista WHERE 1=1 ");

        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append("AND (UPPER(nome) LIKE UPPER(?) OR cpf LIKE ?) ");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            if (filtro != null && !filtro.trim().isEmpty()) {
                String like = "%" + filtro.trim() + "%";
                stmt.setString(1, like);
                stmt.setString(2, like);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public boolean possuiFreteAtivo(Long idMotorista, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM frete WHERE id_motorista = ? " +
                "AND status IN ('SAIDA_CONFIRMADA', 'EM_TRANSITO')";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idMotorista);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean possuiFreteEmitido(Long idMotorista, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM frete WHERE id_motorista = ? " +
                "AND status = 'EMITIDO'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idMotorista);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void excluir(Long id, Connection conn) throws SQLException {
        String sql = "DELETE FROM motorista WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Motorista mapear(ResultSet rs) throws SQLException {
        Motorista motorista = new Motorista();
        motorista.setId(rs.getLong("id"));
        motorista.setNome(rs.getString("nome"));
        motorista.setCpf(rs.getString("cpf"));
        motorista.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
        motorista.setTelefone(rs.getString("telefone"));
        motorista.setNumeroCnh(rs.getString("cnh_numero"));
        motorista.setCategoriaCnh(CategoriaCnh.fromString(rs.getString("cnh_categoria")));
        motorista.setDataValidadeCnh(rs.getDate("cnh_validade").toLocalDate());
        motorista.setTipoVinculo(TipoVinculoMotorista.fromString(rs.getString("tipo_vinculo")));
        motorista.setStatus(StatusMotorista.fromString(rs.getString("status")));
        return motorista;
    }
}