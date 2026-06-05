package br.com.gwfrete.dao;

import br.com.gwfrete.model.PrecoRota;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PrecoRotaDAO {

    private static final String COLUNAS =
            "id, municipio_origem, uf_origem, municipio_destino, uf_destino, " +
            "valor_base, valor_por_kg";

    public void inserir(PrecoRota precoRota, Connection conn) throws SQLException {
        String sql = "INSERT INTO tabela_frete " +
                "(municipio_origem, uf_origem, municipio_destino, uf_destino, valor_base, valor_por_kg) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, precoRota.getMunicipioOrigem());
            stmt.setString(2, precoRota.getUfOrigem());
            stmt.setString(3, precoRota.getMunicipioDestino());
            stmt.setString(4, precoRota.getUfDestino());
            stmt.setBigDecimal(5, precoRota.getValorBase());
            stmt.setBigDecimal(6, precoRota.getValorPorKg());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    precoRota.setId(rs.getLong(1));
                }
            }
        }
    }

    public void atualizar(PrecoRota precoRota, Connection conn) throws SQLException {
        String sql = "UPDATE tabela_frete SET " +
                "municipio_origem = ?, uf_origem = ?, municipio_destino = ?, uf_destino = ?, " +
                "valor_base = ?, valor_por_kg = ? " +
                "WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, precoRota.getMunicipioOrigem());
            stmt.setString(2, precoRota.getUfOrigem());
            stmt.setString(3, precoRota.getMunicipioDestino());
            stmt.setString(4, precoRota.getUfDestino());
            stmt.setBigDecimal(5, precoRota.getValorBase());
            stmt.setBigDecimal(6, precoRota.getValorPorKg());
            stmt.setLong(7, precoRota.getId());
            stmt.executeUpdate();
        }
    }

    public void excluir(Long id, Connection conn) throws SQLException {
        String sql = "DELETE FROM tabela_frete WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public PrecoRota buscarPorId(Long id, Connection conn) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM tabela_frete WHERE id = ?";

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

    public PrecoRota buscarPorRota(String municipioOrigem, String ufOrigem,
                                   String municipioDestino, String ufDestino,
                                   Connection conn) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM tabela_frete " +
                "WHERE UPPER(municipio_origem) = UPPER(?) AND UPPER(uf_origem) = UPPER(?) " +
                "AND UPPER(municipio_destino) = UPPER(?) AND UPPER(uf_destino) = UPPER(?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, municipioOrigem);
            stmt.setString(2, ufOrigem);
            stmt.setString(3, municipioDestino);
            stmt.setString(4, ufDestino);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public List<PrecoRota> listar(String filtro, int pagina, int itensPorPagina,
                                   Connection conn) throws SQLException {
        List<PrecoRota> rotas = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT ").append(COLUNAS)
                .append(" FROM tabela_frete WHERE 1=1 ");

        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append("AND (UPPER(municipio_origem) LIKE UPPER(?) " +
                       "OR UPPER(municipio_destino) LIKE UPPER(?)) ");
        }

        sql.append("ORDER BY municipio_origem ASC, municipio_destino ASC ");
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
                    rotas.add(mapear(rs));
                }
            }
        }
        return rotas;
    }

    public int contarTotal(String filtro, Connection conn) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM tabela_frete WHERE 1=1 ");

        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append("AND (UPPER(municipio_origem) LIKE UPPER(?) " +
                       "OR UPPER(municipio_destino) LIKE UPPER(?)) ");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            if (filtro != null && !filtro.trim().isEmpty()) {
                String like = "%" + filtro.trim() + "%";
                stmt.setString(1, like);
                stmt.setString(2, like);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    private PrecoRota mapear(ResultSet rs) throws SQLException {
        PrecoRota precoRota = new PrecoRota();
        precoRota.setId(rs.getLong("id"));
        precoRota.setMunicipioOrigem(rs.getString("municipio_origem"));
        precoRota.setUfOrigem(rs.getString("uf_origem"));
        precoRota.setMunicipioDestino(rs.getString("municipio_destino"));
        precoRota.setUfDestino(rs.getString("uf_destino"));
        precoRota.setValorBase(rs.getBigDecimal("valor_base"));
        precoRota.setValorPorKg(rs.getBigDecimal("valor_por_kg"));
        return precoRota;
    }
}