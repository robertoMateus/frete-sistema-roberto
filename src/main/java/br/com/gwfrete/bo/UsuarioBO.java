package br.com.gwfrete.bo;

import br.com.gwfrete.dao.UsuarioDAO;
import br.com.gwfrete.exception.NegocioException;
import br.com.gwfrete.exception.UsuarioException;
import br.com.gwfrete.model.Usuario;
import br.com.gwfrete.util.ConexaoPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioBO {

    private static final Logger LOGGER = Logger.getLogger(UsuarioBO.class.getName());
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario autenticar(String login, String senha) throws NegocioException {
        if (login == null || login.trim().isEmpty()) {
            throw new UsuarioException("O login é obrigatório.");
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new UsuarioException("A senha é obrigatória.");
        }

        try (Connection conn = ConexaoPool.getConexao()) {
            Usuario usuario = usuarioDAO.buscarPorLogin(login.trim(), conn);

            if (usuario == null || !usuario.getSenha().equals(senha)) {
                throw new UsuarioException("Login ou senha inválidos.");
            }

            return usuario;

        } catch (NegocioException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao autenticar usuário.", e);
            throw new UsuarioException("Erro inesperado ao realizar login.");
        }
    }
}