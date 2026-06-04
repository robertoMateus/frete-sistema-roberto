package br.com.gwfrete.dao;

import br.com.gwfrete.model.Cliente;
import br.com.gwfrete.model.StatusCliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private static final String COLUNAS = 
        "id, razao_social, nome_fantasia, cnpj, inscricao_estadual, " +
        "logradouro, numero, complemento, bairro, municipio, uf, cep, " +
        "telefone, email, status";

    public void inserir(Cliente cliente, Connection conn) throws SQLException {
        String sql = "INSERT INTO cliente (razao_social, nome_fantasia, cnpj, inscricao_estadual, " +
                "logradouro, numero, complemento, bairro, municipio, uf, cep, " +
                "telefone, email, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cliente.getRazaoSocial());
            stmt.setString(2, cliente.getNomeFantasia());
            stmt.setString(3, cliente.getCnpj());
            stmt.setString(4, cliente.getInscricaoEstadual());
            stmt.setString(5, cliente.getLogradouro());
            stmt.setString(6, cliente.getNumero());
            stmt.setString(7, cliente.getComplemento());
            stmt.setString(8, cliente.getBairro());
            stmt.setString(9, cliente.getMunicipio());
            stmt.setString(10, cliente.getUf());
            stmt.setString(11, cliente.getCep());
            stmt.setString(12, cliente.getTelefone());
            stmt.setString(13, cliente.getEmail());
            stmt.setString(14, cliente.getStatus().name());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getLong(1));
                }
            }
        }
    }

    public void atualizar(Cliente cliente, Connection conn) throws SQLException {
        String sql = "UPDATE cliente SET razao_social = ?, nome_fantasia = ?, cnpj = ?, " +
                "inscricao_estadual = ?, logradouro = ?, numero = ?, complemento = ?, " +
                "bairro = ?, municipio = ?, uf = ?, cep = ?, telefone = ?, email = ?, " +
                "status = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getRazaoSocial());
            stmt.setString(2, cliente.getNomeFantasia());
            stmt.setString(3, cliente.getCnpj());
            stmt.setString(4, cliente.getInscricaoEstadual());
            stmt.setString(5, cliente.getLogradouro());
            stmt.setString(6, cliente.getNumero());
            stmt.setString(7, cliente.getComplemento());
            stmt.setString(8, cliente.getBairro());
            stmt.setString(9, cliente.getMunicipio());
            stmt.setString(10, cliente.getUf());
            stmt.setString(11, cliente.getCep());
            stmt.setString(12, cliente.getTelefone());
            stmt.setString(13, cliente.getEmail());
            stmt.setString(14, cliente.getStatus().name());
            stmt.setLong(15, cliente.getId());

            stmt.executeUpdate();
        }
    }

    public Cliente buscarPorId(Long id, Connection conn) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM cliente WHERE id = ?";

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

    public Cliente buscarPorCnpj(String cnpj, Connection conn) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM cliente WHERE cnpj = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cnpj);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public List<Cliente> listar(String filtro, int pagina, int itensPorPagina, Connection conn) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT " + COLUNAS + " FROM cliente WHERE 1=1 ");

        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append("AND (UPPER(razao_social) LIKE UPPER(?) OR UPPER(nome_fantasia) LIKE UPPER(?) OR cnpj LIKE ?) ");
        }

        sql.append("ORDER BY razao_social ASC ");
        sql.append("LIMIT ? OFFSET ?");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;

            if (filtro != null && !filtro.trim().isEmpty()) {
                String like = "%" + filtro.trim() + "%";
                stmt.setString(idx++, like);
                stmt.setString(idx++, like);
                stmt.setString(idx++, like);
            }

            stmt.setInt(idx++, itensPorPagina);
            stmt.setInt(idx, (pagina - 1) * itensPorPagina);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapear(rs));
                }
            }
        }
        return clientes;
    }

    public int contarTotal(String filtro, Connection conn) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM cliente WHERE 1=1 ");

        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append("AND (UPPER(razao_social) LIKE UPPER(?) OR UPPER(nome_fantasia) LIKE UPPER(?) OR cnpj LIKE ?) ");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            if (filtro != null && !filtro.trim().isEmpty()) {
                String like = "%" + filtro.trim() + "%";
                stmt.setString(1, like);
                stmt.setString(2, like);
                stmt.setString(3, like);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public boolean possuiFretes(Long idCliente, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM frete WHERE id_remetente = ? OR id_destinatario = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idCliente);
            stmt.setLong(2, idCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void excluir(Long id, Connection conn) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getLong("id"));
        cliente.setRazaoSocial(rs.getString("razao_social"));
        cliente.setNomeFantasia(rs.getString("nome_fantasia"));
        cliente.setCnpj(rs.getString("cnpj"));
        cliente.setInscricaoEstadual(rs.getString("inscricao_estadual"));
        cliente.setLogradouro(rs.getString("logradouro"));
        cliente.setNumero(rs.getString("numero"));
        cliente.setComplemento(rs.getString("complemento"));
        cliente.setBairro(rs.getString("bairro"));
        cliente.setMunicipio(rs.getString("municipio"));
        cliente.setUf(rs.getString("uf"));
        cliente.setCep(rs.getString("cep"));
        cliente.setTelefone(rs.getString("telefone"));
        cliente.setEmail(rs.getString("email"));
        cliente.setStatus(StatusCliente.fromString(rs.getString("status")));
        return cliente;
    }
}