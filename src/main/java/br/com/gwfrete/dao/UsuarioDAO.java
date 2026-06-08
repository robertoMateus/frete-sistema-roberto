package br.com.gwfrete.dao;

import br.com.gwfrete.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    private static final String COLUNAS = "id, nome, login, senha, ativo";

    public Usuario buscarPorLogin(String login, Connection conn) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM usuario WHERE login = ? AND ativo = TRUE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setLogin(rs.getString("login"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setAtivo(rs.getBoolean("ativo"));
        return usuario;
    }
}