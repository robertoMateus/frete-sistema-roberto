package br.com.gwfrete.dao;

import br.com.gwfrete.model.ManutencaoVeiculo;
import br.com.gwfrete.model.TipoManutencao;
import br.com.gwfrete.model.TipoVeiculo;
import br.com.gwfrete.model.Veiculo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ManutencaoVeiculoDAO {

    private static final String COLUNAS = "m.id, m.id_veiculo, m.tipo, m.descricao, m.data_inicio, m.data_fim, m.custo, v.id as veiculo_id, v.placa, v.tipo as tipo_veiculo";

    private static final String FROM_JOIN = "FROM manutencao_veiculo m JOIN veiculo v ON v.id = m.id_veiculo ";

    public void inserir(ManutencaoVeiculo manutencao, Connection conn) throws SQLException {
        String sql = "INSERT INTO manutencao_veiculo " +
                "(id_veiculo, tipo, descricao, data_inicio, data_fim, custo) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, manutencao.getVeiculo().getId());
            stmt.setString(2, manutencao.getTipo().name());
            stmt.setString(3, manutencao.getDescricao());
            stmt.setDate(4, Date.valueOf(manutencao.getDataInicio()));
            stmt.setDate(5, manutencao.getDataFim() != null
                    ? Date.valueOf(manutencao.getDataFim())
                    : null);
            stmt.setBigDecimal(6, manutencao.getCusto());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    manutencao.setId(rs.getLong(1));
                }
            }
        }
    }

    public void atualizar(ManutencaoVeiculo manutencao, Connection conn) throws SQLException {
        String sql = "UPDATE manutencao_veiculo SET " +
                "tipo = ?, descricao = ?, data_inicio = ?, data_fim = ?, custo = ? " +
                "WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, manutencao.getTipo().name());
            stmt.setString(2, manutencao.getDescricao());
            stmt.setDate(3, Date.valueOf(manutencao.getDataInicio()));
            stmt.setDate(4, manutencao.getDataFim() != null
                    ? Date.valueOf(manutencao.getDataFim())
                    : null);
            stmt.setBigDecimal(5, manutencao.getCusto());
            stmt.setLong(6, manutencao.getId());
            stmt.executeUpdate();
        }
    }

    public void excluir(Long id, Connection conn) throws SQLException {
        String sql = "DELETE FROM manutencao_veiculo WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public ManutencaoVeiculo buscarPorId(Long id, Connection conn) throws SQLException {
        String sql = "SELECT " + COLUNAS + " " + FROM_JOIN + "WHERE m.id = ?";

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

    public List<ManutencaoVeiculo> listarPorVeiculo(Long idVeiculo, int pagina, int itensPorPagina, Connection conn)
            throws SQLException {
        return listarPorVeiculo(idVeiculo, null, pagina, itensPorPagina, conn);
    }

    public List<ManutencaoVeiculo> listarPorVeiculo(Long idVeiculo, String filtro, int pagina, int itensPorPagina,
            Connection conn)
            throws SQLException {
        List<ManutencaoVeiculo> manutencoes = new ArrayList<>();
        int offset = (pagina - 1) * itensPorPagina;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(COLUNAS).append(" ").append(FROM_JOIN)
                .append("WHERE m.id_veiculo = ? ");

        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append(
                    "AND (UPPER(v.placa) LIKE UPPER(?) OR UPPER(m.descricao) LIKE UPPER(?) OR UPPER(m.tipo) LIKE UPPER(?)) ");
        }

        sql.append("ORDER BY m.data_inicio DESC LIMIT ? OFFSET ?");
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            stmt.setLong(idx++, idVeiculo);
            if (filtro != null && !filtro.trim().isEmpty()) {
                String like = "%" + filtro.trim() + "%";
                stmt.setString(idx++, like);
                stmt.setString(idx++, like);
                stmt.setString(idx++, like);
            }
            stmt.setInt(idx++, itensPorPagina);
            stmt.setInt(idx, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    manutencoes.add(mapear(rs));
            }
        }
        return manutencoes;
    }

    public List<ManutencaoVeiculo> listarEmAberto(int pagina, int itensPorPagina, Connection conn) throws SQLException {
        return listarEmAberto(null, pagina, itensPorPagina, conn);
    }

    public List<ManutencaoVeiculo> listarEmAberto(String filtro, int pagina, int itensPorPagina, Connection conn)
            throws SQLException {
        List<ManutencaoVeiculo> manutencoes = new ArrayList<>();
        int offset = (pagina - 1) * itensPorPagina;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(COLUNAS).append(" ").append(FROM_JOIN)
                .append("WHERE m.data_fim IS NULL ");

        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append(
                    "AND (UPPER(v.placa) LIKE UPPER(?) OR UPPER(m.descricao) LIKE UPPER(?) OR UPPER(m.tipo) LIKE UPPER(?)) ");
        }

        sql.append("ORDER BY m.data_inicio ASC LIMIT ? OFFSET ?");
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (filtro != null && !filtro.trim().isEmpty()) {
                String like = "%" + filtro.trim() + "%";
                stmt.setString(idx++, like);
                stmt.setString(idx++, like);
                stmt.setString(idx++, like);
            }
            stmt.setInt(idx++, itensPorPagina);
            stmt.setInt(idx, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    manutencoes.add(mapear(rs));
            }
        }
        return manutencoes;
    }

    public boolean possuiManutencaoEmAberto(Long idVeiculo, Long ignorarId, Connection conn) throws SQLException {
        String sql = ignorarId != null
                ? "SELECT COUNT(*) FROM manutencao_veiculo WHERE id_veiculo = ? AND data_fim IS NULL AND id != ?"
                : "SELECT COUNT(*) FROM manutencao_veiculo WHERE id_veiculo = ? AND data_fim IS NULL";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idVeiculo);
            if (ignorarId != null)
                stmt.setLong(2, ignorarId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public int contarEmAberto(Connection conn) throws SQLException {
        return contarEmAberto(null, conn);
    }

    public int contarEmAberto(String filtro, Connection conn) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) ").append(FROM_JOIN)
                .append("WHERE m.data_fim IS NULL ");
        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append(
                    "AND (UPPER(v.placa) LIKE UPPER(?) OR UPPER(m.descricao) LIKE UPPER(?) OR UPPER(m.tipo) LIKE UPPER(?)) ");
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            if (filtro != null && !filtro.trim().isEmpty()) {
                String like = "%" + filtro.trim() + "%";
                stmt.setString(1, like);
                stmt.setString(2, like);
                stmt.setString(3, like);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public int contarPorVeiculo(Long idVeiculo, Connection conn) throws SQLException {
        return contarPorVeiculo(idVeiculo, null, conn);
    }

    public int contarPorVeiculo(Long idVeiculo, String filtro, Connection conn) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) ").append(FROM_JOIN)
                .append("WHERE m.id_veiculo = ? ");
        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append(
                    "AND (UPPER(v.placa) LIKE UPPER(?) OR UPPER(m.descricao) LIKE UPPER(?) OR UPPER(m.tipo) LIKE UPPER(?)) ");
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            stmt.setLong(idx++, idVeiculo);
            if (filtro != null && !filtro.trim().isEmpty()) {
                String like = "%" + filtro.trim() + "%";
                stmt.setString(idx++, like);
                stmt.setString(idx++, like);
                stmt.setString(idx++, like);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public List<ManutencaoVeiculo> listarTodas(String filtro, int pagina, int itensPorPagina, Connection conn)
            throws SQLException {

        List<ManutencaoVeiculo> manutencoes = new ArrayList<>();
        int offset = (pagina - 1) * itensPorPagina;

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(COLUNAS).append(" ").append(FROM_JOIN);

        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append(
                    "WHERE (UPPER(v.placa) LIKE UPPER(?) OR UPPER(m.descricao) LIKE UPPER(?) OR UPPER(m.tipo) LIKE UPPER(?)) ");
        }

        sql.append("ORDER BY m.data_inicio DESC, m.id DESC LIMIT ? OFFSET ?");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (filtro != null && !filtro.trim().isEmpty()) {
                String like = "%" + filtro.trim() + "%";
                stmt.setString(idx++, like);
                stmt.setString(idx++, like);
                stmt.setString(idx++, like);
            }
            stmt.setInt(idx++, itensPorPagina);
            stmt.setInt(idx, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    manutencoes.add(mapear(rs));
            }
        }
        return manutencoes;
    }

    public int contarTodas(String filtro, Connection conn) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) ").append(FROM_JOIN);

        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append(
                    "WHERE (UPPER(v.placa) LIKE UPPER(?) OR UPPER(m.descricao) LIKE UPPER(?) OR UPPER(m.tipo) LIKE UPPER(?)) ");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            if (filtro != null && !filtro.trim().isEmpty()) {
                String like = "%" + filtro.trim() + "%";
                stmt.setString(1, like);
                stmt.setString(2, like);
                stmt.setString(3, like);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private ManutencaoVeiculo mapear(ResultSet rs) throws SQLException {
        ManutencaoVeiculo manutencao = new ManutencaoVeiculo();
        manutencao.setId(rs.getLong("id"));
        manutencao.setTipo(TipoManutencao.fromString(rs.getString("tipo")));
        manutencao.setDescricao(rs.getString("descricao"));
        manutencao.setDataInicio(rs.getDate("data_inicio").toLocalDate());
        Date dataFim = rs.getDate("data_fim");
        if (dataFim != null)
            manutencao.setDataFim(dataFim.toLocalDate());
        manutencao.setCusto(rs.getBigDecimal("custo"));

        Veiculo veiculo = new Veiculo();
        veiculo.setId(rs.getLong("veiculo_id"));
        veiculo.setPlaca(rs.getString("placa"));
        veiculo.setTipoVeiculo(TipoVeiculo.fromString(rs.getString("tipo_veiculo")));
        manutencao.setVeiculo(veiculo);

        return manutencao;
    }
}