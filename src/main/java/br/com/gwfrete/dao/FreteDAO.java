package br.com.gwfrete.dao;

import br.com.gwfrete.model.Frete;
import br.com.gwfrete.model.StatusFrete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FreteDAO {

    private static final String COLUNAS = "id, numero, id_remetente, id_destinatario, id_motorista, id_veiculo, " +
            "municipio_origem, uf_origem, municipio_destino, uf_destino, " +
            "descricao_carga, peso_kg, volumes, valor_frete, aliquota_icms, " +
            "valor_icms, valor_total, status, " +
            "data_emissao, data_previsao_entrega, data_saida, data_entrega";

    public void inserir(Frete frete, Connection conn) throws SQLException {
        String sql = "INSERT INTO frete (numero, id_remetente, id_destinatario, id_motorista, id_veiculo, " +
                "municipio_origem, uf_origem, municipio_destino, uf_destino, " +
                "descricao_carga, peso_kg, volumes, valor_frete, aliquota_icms, " +
                "valor_icms, valor_total, status, data_emissao, data_previsao_entrega) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, frete.getNumeroFrete());
            stmt.setLong(2, frete.getRemetente().getId());
            stmt.setLong(3, frete.getDestinatario().getId());
            stmt.setLong(4, frete.getMotorista().getId());
            stmt.setLong(5, frete.getVeiculo().getId());
            stmt.setString(6, frete.getMunicipioOrigem());
            stmt.setString(7, frete.getUfOrigem());
            stmt.setString(8, frete.getMunicipioDestino());
            stmt.setString(9, frete.getUfDestino());
            stmt.setString(10, frete.getDescricaoCarga());
            stmt.setBigDecimal(11, frete.getPesoCarga());
            stmt.setInt(12, frete.getVolumeCarga());
            stmt.setBigDecimal(13, frete.getValorFrete());
            stmt.setBigDecimal(14, frete.getAliquotaIcms());
            stmt.setBigDecimal(15, frete.getValorIcms());
            stmt.setBigDecimal(16, frete.getValorTotal());
            stmt.setString(17, frete.getStatus().name());
            stmt.setTimestamp(18, Timestamp.valueOf(frete.getDataEmissao()));
            stmt.setTimestamp(19, Timestamp.valueOf(frete.getDataPrevisaoEntrega()));

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    frete.setId(rs.getLong(1));
                }
            }
        }
    }

    public void atualizarStatus(Long id, StatusFrete status, Connection conn) throws SQLException {
        String sql = "UPDATE frete SET status = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    public void atualizarDataSaida(Long id, java.time.LocalDateTime dataSaida, Connection conn) throws SQLException {
        String sql = "UPDATE frete SET data_saida = ?, status = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(dataSaida));
            stmt.setString(2, StatusFrete.SAIDA_CONFIRMADA.name());
            stmt.setLong(3, id);
            stmt.executeUpdate();
        }
    }

    public void atualizarDataEntrega(Long id, java.time.LocalDateTime dataEntrega, Connection conn)
            throws SQLException {
        String sql = "UPDATE frete SET data_entrega = ?, status = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(dataEntrega));
            stmt.setString(2, StatusFrete.ENTREGUE.name());
            stmt.setLong(3, id);
            stmt.executeUpdate();
        }
    }

    public Frete buscarPorId(Long id, Connection conn) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM frete WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs, conn);
                }
            }
        }
        return null;
    }

    public Frete buscarPorNumero(String numero, Connection conn) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM frete WHERE numero = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs, conn);
                }
            }
        }
        return null;
    }

    public List<Frete> listar(String filtro, int pagina, int itensPorPagina, Connection conn) throws SQLException {
        List<Frete> fretes = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT ").append(COLUNAS).append(" FROM frete WHERE 1=1 ");

        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append("AND (UPPER(numero) LIKE UPPER(?) OR UPPER(municipio_destino) LIKE UPPER(?)) ");
        }

        sql.append("ORDER BY data_emissao DESC ");
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
                    fretes.add(mapear(rs, conn));
                }
            }
        }
        return fretes;
    }

    public List<Frete> listarEmAberto(Connection conn) throws SQLException {
        List<Frete> fretes = new ArrayList<>();

        String sql = "SELECT " + COLUNAS + " FROM frete " +
                "WHERE status NOT IN ('ENTREGUE', 'NAO_ENTREGUE', 'CANCELADO') " +
                "ORDER BY data_previsao_entrega ASC";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                fretes.add(mapear(rs, conn));
            }
        }
        return fretes;
    }

    public List<Frete> listarPorMotoristaEData(Long idMotorista, java.time.LocalDate data, Connection conn)
            throws SQLException {
        List<Frete> fretes = new ArrayList<>();

        String sql = "SELECT " + COLUNAS + " FROM frete " +
                "WHERE id_motorista = ? AND DATE(data_emissao) = ? " +
                "ORDER BY data_emissao ASC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idMotorista);
            stmt.setDate(2, java.sql.Date.valueOf(data));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    fretes.add(mapear(rs, conn));
                }
            }
        }
        return fretes;
    }

    public int contarTotal(String filtro, Connection conn) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM frete WHERE 1=1 ");

        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append("AND (UPPER(numero) LIKE UPPER(?) OR UPPER(municipio_destino) LIKE UPPER(?)) ");
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

    public boolean existeNumero(String numero, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM frete WHERE numero = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public long buscarProximoSequencial(Connection conn) throws SQLException {
        String sql = "SELECT nextval('seq_frete_numero')";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new SQLException(
                    "Não foi possível obter o próximo valor da sequence seq_frete_numero.");
        }
    }

    public boolean motoristaTemFreteAtivo(Long idMotorista, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM frete WHERE id_motorista = ? " +
                "AND status IN ('SAIDA_CONFIRMADA', 'EM_TRANSITO')";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idMotorista);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private Frete mapear(ResultSet rs, Connection conn) throws SQLException {
        Frete frete = new Frete();
        frete.setId(rs.getLong("id"));
        frete.setNumeroFrete(rs.getString("numero"));
        frete.setMunicipioOrigem(rs.getString("municipio_origem"));
        frete.setUfOrigem(rs.getString("uf_origem"));
        frete.setMunicipioDestino(rs.getString("municipio_destino"));
        frete.setUfDestino(rs.getString("uf_destino"));
        frete.setDescricaoCarga(rs.getString("descricao_carga"));
        frete.setPesoCarga(rs.getBigDecimal("peso_kg"));
        frete.setVolumeCarga(rs.getInt("volumes"));
        frete.setValorFrete(rs.getBigDecimal("valor_frete"));
        frete.setAliquotaIcms(rs.getBigDecimal("aliquota_icms"));
        frete.setValorIcms(rs.getBigDecimal("valor_icms"));
        frete.setValorTotal(rs.getBigDecimal("valor_total"));
        frete.setStatus(StatusFrete.fromString(rs.getString("status")));

        Timestamp dataEmissao = rs.getTimestamp("data_emissao");
        if (dataEmissao != null)
            frete.setDataEmissao(dataEmissao.toLocalDateTime());

        Timestamp dataPrevisao = rs.getTimestamp("data_previsao_entrega");
        if (dataPrevisao != null)
            frete.setDataPrevisaoEntrega(dataPrevisao.toLocalDateTime());

        Timestamp dataSaida = rs.getTimestamp("data_saida");
        if (dataSaida != null)
            frete.setDataSaida(dataSaida.toLocalDateTime());

        Timestamp dataEntrega = rs.getTimestamp("data_entrega");
        if (dataEntrega != null)
            frete.setDataEntrega(dataEntrega.toLocalDateTime());

        // carrega objetos relacionados com apenas o id — o BO busca completo quando
        // precisar
        ClienteDAO clienteDAO = new ClienteDAO();
        frete.setRemetente(clienteDAO.buscarPorId(rs.getLong("id_remetente"), conn));
        frete.setDestinatario(clienteDAO.buscarPorId(rs.getLong("id_destinatario"), conn));

        MotoristaDAO motoristaDAO = new MotoristaDAO();
        frete.setMotorista(motoristaDAO.buscarPorId(rs.getLong("id_motorista"), conn));

        VeiculoDAO veiculoDAO = new VeiculoDAO();
        frete.setVeiculo(veiculoDAO.buscarPorId(rs.getLong("id_veiculo"), conn));

        return frete;
    }
}