package br.com.gwfrete.dao;

import br.com.gwfrete.model.StatusVeiculo;
import br.com.gwfrete.model.TipoVeiculo;
import br.com.gwfrete.model.Veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO {

    private static final String COLUNAS = "id, placa, rntrc, ano_fabricacao, tipo, " +
            "tara_kg, capacidade_kg, volume_m3, status";

    public void inserir(Veiculo veiculo, Connection conn) throws SQLException {
        String sql = "INSERT INTO veiculo (placa, rntrc, ano_fabricacao, tipo, " +
                "tara_kg, capacidade_kg, volume_m3, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, veiculo.getRntrc());
            stmt.setInt(3, veiculo.getAnoFabricacao());
            stmt.setString(4, veiculo.getTipoVeiculo().name());
            stmt.setDouble(5, veiculo.getTara());
            stmt.setDouble(6, veiculo.getCapacidadeCarga());
            stmt.setDouble(7, veiculo.getVolume());
            stmt.setString(8, veiculo.getStatus().name());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    veiculo.setId(rs.getLong(1));
                }
            }
        }
    }

    public void atualizar(Veiculo veiculo, Connection conn) throws SQLException {
        String sql = "UPDATE veiculo SET placa = ?, rntrc = ?, ano_fabricacao = ?, tipo = ?, " +
                "tara_kg = ?, capacidade_kg = ?, volume_m3 = ?, status = ? " +
                "WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, veiculo.getRntrc());
            stmt.setInt(3, veiculo.getAnoFabricacao());
            stmt.setString(4, veiculo.getTipoVeiculo().name());
            stmt.setDouble(5, veiculo.getTara());
            stmt.setDouble(6, veiculo.getCapacidadeCarga());
            stmt.setDouble(7, veiculo.getVolume());
            stmt.setString(8, veiculo.getStatus().name());
            stmt.setLong(9, veiculo.getId());

            stmt.executeUpdate();
        }
    }

    public void atualizarStatus(Long id, StatusVeiculo status, Connection conn) throws SQLException {
        String sql = "UPDATE veiculo SET status = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    public Veiculo buscarPorId(Long id, Connection conn) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM veiculo WHERE id = ?";

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

    public Veiculo buscarPorPlaca(String placa, Connection conn) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM veiculo WHERE placa = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, placa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public Veiculo buscarPorRntrc(String rntrc, Connection conn) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM veiculo WHERE rntrc = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rntrc);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public List<Veiculo> listar(String filtro, int pagina, int itensPorPagina, Connection conn) throws SQLException {
        List<Veiculo> veiculos = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT ").append(COLUNAS).append(" FROM veiculo WHERE 1=1 ");

        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append("AND (UPPER(placa) LIKE UPPER(?) OR rntrc LIKE ?) ");
        }

        sql.append("ORDER BY placa ASC ");
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
                    veiculos.add(mapear(rs));
                }
            }
        }
        return veiculos;
    }

    public int contarTotal(String filtro, Connection conn) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM veiculo WHERE 1=1 ");

        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append("AND (UPPER(placa) LIKE UPPER(?) OR rntrc LIKE ?) ");
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

    public boolean possuiFreteEmTransito(Long idVeiculo, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM frete WHERE id_veiculo = ? " +
                "AND status = 'EM_TRANSITO'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idVeiculo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void excluir(Long id, Connection conn) throws SQLException {
        String sql = "DELETE FROM veiculo WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Veiculo mapear(ResultSet rs) throws SQLException {
        Veiculo veiculo = new Veiculo();
        veiculo.setId(rs.getLong("id"));
        veiculo.setPlaca(rs.getString("placa"));
        veiculo.setRntrc(rs.getString("rntrc"));
        veiculo.setAnoFabricacao(rs.getInt("ano_fabricacao"));
        veiculo.setTipoVeiculo(TipoVeiculo.fromString(rs.getString("tipo")));
        veiculo.setTara(rs.getDouble("tara_kg"));
        veiculo.setCapacidadeCarga(rs.getDouble("capacidade_kg"));
        veiculo.setVolume(rs.getDouble("volume_m3"));
        veiculo.setStatus(StatusVeiculo.fromString(rs.getString("status")));
        return veiculo;
    }
}