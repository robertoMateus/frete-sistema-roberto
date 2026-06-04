package br.com.gwfrete.dao;

import br.com.gwfrete.model.Frete;
import br.com.gwfrete.model.OcorrenciaFrete;
import br.com.gwfrete.model.TipoOcorrencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OcorrenciaFreteDAO {

    private static final String COLUNAS =
            "id, id_frete, tipo, data_hora, municipio, uf, " +
            "descricao, nome_recebedor, documento_recebedor";

    public void inserir(OcorrenciaFrete ocorrencia, Connection conn) throws SQLException {
        String sql = "INSERT INTO ocorrencia_frete (id_frete, tipo, data_hora, municipio, uf, " +
                "descricao, nome_recebedor, documento_recebedor) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, ocorrencia.getFrete().getId());
            stmt.setString(2, ocorrencia.getTipo().name());
            stmt.setTimestamp(3, Timestamp.valueOf(ocorrencia.getDataHoraOcorrencia()));
            stmt.setString(4, ocorrencia.getMunicipio());
            stmt.setString(5, ocorrencia.getUf());
            stmt.setString(6, ocorrencia.getDescricao());
            stmt.setString(7, ocorrencia.getNomeRecebedor());
            stmt.setString(8, ocorrencia.getDocumentoRecebedor());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ocorrencia.setId(rs.getLong(1));
                }
            }
        }
    }

    public List<OcorrenciaFrete> listarPorFrete(Long idFrete, Connection conn) throws SQLException {
        List<OcorrenciaFrete> ocorrencias = new ArrayList<>();

        String sql = "SELECT " + COLUNAS + " FROM ocorrencia_frete " +
                "WHERE id_frete = ? " +
                "ORDER BY data_hora ASC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idFrete);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ocorrencias.add(mapear(rs));
                }
            }
        }
        return ocorrencias;
    }

    public OcorrenciaFrete buscarUltimaPorFrete(Long idFrete, Connection conn) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM ocorrencia_frete " +
                "WHERE id_frete = ? " +
                "ORDER BY data_hora DESC " +
                "LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idFrete);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    private OcorrenciaFrete mapear(ResultSet rs) throws SQLException {
        OcorrenciaFrete ocorrencia = new OcorrenciaFrete();
        ocorrencia.setId(rs.getLong("id"));
        ocorrencia.setTipo(TipoOcorrencia.fromString(rs.getString("tipo")));
        ocorrencia.setDataHoraOcorrencia(rs.getTimestamp("data_hora").toLocalDateTime());
        ocorrencia.setMunicipio(rs.getString("municipio"));
        ocorrencia.setUf(rs.getString("uf"));
        ocorrencia.setDescricao(rs.getString("descricao"));
        ocorrencia.setNomeRecebedor(rs.getString("nome_recebedor"));
        ocorrencia.setDocumentoRecebedor(rs.getString("documento_recebedor"));

        Frete frete = new Frete();
        frete.setId(rs.getLong("id_frete"));
        ocorrencia.setFrete(frete);

        return ocorrencia;
    }
}