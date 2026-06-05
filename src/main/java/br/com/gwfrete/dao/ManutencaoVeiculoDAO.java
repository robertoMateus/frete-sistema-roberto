package br.com.gwfrete.dao;

import br.com.gwfrete.model.ManutencaoVeiculo;
import br.com.gwfrete.model.TipoManutencao;
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

    private static final String COLUNAS =
            "id, id_veiculo, tipo, descricao, data_inicio, data_fim, custo";

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
                    ? Date.valueOf(manutencao.getDataFim()) : null);
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
                    ? Date.valueOf(manutencao.getDataFim()) : null);
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
        String sql = "SELECT " + COLUNAS + " FROM manutencao_veiculo WHERE id = ?";

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

    public List<ManutencaoVeiculo> listarPorVeiculo(Long idVeiculo, Connection conn) throws SQLException {
        List<ManutencaoVeiculo> manutencoes = new ArrayList<>();

        String sql = "SELECT " + COLUNAS + " FROM manutencao_veiculo " +
                "WHERE id_veiculo = ? " +
                "ORDER BY data_inicio DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idVeiculo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    manutencoes.add(mapear(rs));
                }
            }
        }
        return manutencoes;
    }

    public List<ManutencaoVeiculo> listarEmAberto(Connection conn) throws SQLException {
        List<ManutencaoVeiculo> manutencoes = new ArrayList<>();

        String sql = "SELECT " + COLUNAS + " FROM manutencao_veiculo " +
                "WHERE data_fim IS NULL " +
                "ORDER BY data_inicio ASC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    manutencoes.add(mapear(rs));
                }
            }
        }
        return manutencoes;
    }

    public boolean possuiManutencaoEmAberto(Long idVeiculo, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM manutencao_veiculo " +
                "WHERE id_veiculo = ? AND data_fim IS NULL";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idVeiculo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private ManutencaoVeiculo mapear(ResultSet rs) throws SQLException {
        ManutencaoVeiculo manutencao = new ManutencaoVeiculo();
        manutencao.setId(rs.getLong("id"));
        manutencao.setTipo(TipoManutencao.fromString(rs.getString("tipo")));
        manutencao.setDescricao(rs.getString("descricao"));
        manutencao.setDataInicio(rs.getDate("data_inicio").toLocalDate());
        Date dataFim = rs.getDate("data_fim");
        if (dataFim != null) manutencao.setDataFim(dataFim.toLocalDate());
        manutencao.setCusto(rs.getBigDecimal("custo"));

        Veiculo veiculo = new Veiculo();
        veiculo.setId(rs.getLong("id_veiculo"));
        manutencao.setVeiculo(veiculo);

        return manutencao;
    }
}