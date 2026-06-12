package br.com.gwfrete.dao;

import br.com.gwfrete.model.Motorista;
import br.com.gwfrete.model.TipoVeiculo;
import br.com.gwfrete.model.Veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RelatorioDAO {

    public Motorista buscarMotoristaComVeiculo(Connection conn, long idMotorista) throws SQLException {
        String sql = "SELECT m.nome, m.cpf, m.cnh_numero, m.cnh_categoria, m.cnh_validade, " +
                "       v.placa, v.tipo " +
                "  FROM motorista m " +
                "  LEFT JOIN frete f ON f.id_motorista = m.id " +
                "       AND f.status NOT IN ('CANCELADO', 'ENTREGUE', 'NAO_ENTREGUE') " +
                "  LEFT JOIN veiculo v ON v.id = f.id_veiculo " +
                " WHERE m.id = ? " +
                " ORDER BY f.data_emissao DESC " +
                " LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idMotorista);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Motorista motorista = new Motorista();
                    motorista.setNome(rs.getString("nome"));
                    motorista.setCpf(rs.getString("cpf"));
                    motorista.setNumeroCnh(rs.getString("cnh_numero"));
                    motorista.setCategoriaCnh(
                            br.com.gwfrete.model.CategoriaCnh.valueOf(rs.getString("cnh_categoria")));

                    String dataValidadeStr = rs.getString("cnh_validade");
                    if (dataValidadeStr != null) {
                        motorista.setDataValidadeCnh(rs.getDate("cnh_validade").toLocalDate());
                    }

                    String placa = rs.getString("placa");
                    if (placa != null) {
                        Veiculo veiculo = new Veiculo();
                        veiculo.setPlaca(placa);
                        veiculo.setTipoVeiculo(TipoVeiculo.valueOf(rs.getString("tipo")));
                        motorista.setVeiculo(veiculo);
                    }

                    return motorista;
                }
                return null;
            }
        }
    }

    public int contarFretesDoMotoristaData(Connection conn, long idMotorista, java.sql.Date data)
            throws SQLException {

        String sql = "SELECT COUNT(*) " +
                "  FROM frete " +
                " WHERE id_motorista = ? " +
                "   AND status NOT IN ('CANCELADO') " +
                "   AND DATE(data_emissao) = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idMotorista);
            ps.setDate(2, data);


            try (ResultSet rs = ps.executeQuery()) {

                rs.next();

                int total = rs.getInt(1);


                return total;
            }
        }
    }

}