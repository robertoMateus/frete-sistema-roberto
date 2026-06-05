package br.com.gwfrete.bo;

import br.com.gwfrete.dao.ClienteDAO;
import br.com.gwfrete.exception.CadastroException;
import br.com.gwfrete.exception.ClienteException;
import br.com.gwfrete.model.Cliente;
import br.com.gwfrete.model.StatusCliente;
import br.com.gwfrete.util.CepUtil;
import br.com.gwfrete.util.CnpjUtil;
import br.com.gwfrete.util.ConexaoPool;
import br.com.gwfrete.util.EmailUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteBO {

    private static final Logger LOGGER = Logger.getLogger(ClienteBO.class.getName());
    private final ClienteDAO clienteDAO = new ClienteDAO();

    public void cadastrar(Cliente cliente) throws CadastroException, ClienteException {
        validarCamposObrigatorios(cliente);
        validarFormatos(cliente);

        try (Connection conn = ConexaoPool.getConexao()) {
            Cliente existente = clienteDAO.buscarPorCnpj(cliente.getCnpj(), conn);
            if (existente != null) {
                throw new CadastroException("O CNPJ informado já está cadastrado.");
            }

            cliente.setStatus(StatusCliente.ATIVO);
            clienteDAO.inserir(cliente, conn);

        } catch (CadastroException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao cadastrar cliente.", e);
            throw new ClienteException("Erro inesperado ao cadastrar cliente.");
        }
    }

    public void atualizar(Cliente cliente) throws CadastroException, ClienteException {
        validarCamposObrigatorios(cliente);
        validarFormatos(cliente);

        try (Connection conn = ConexaoPool.getConexao()) {
            Cliente existente = clienteDAO.buscarPorCnpj(cliente.getCnpj(), conn);
            if (existente != null && !existente.getId().equals(cliente.getId())) {
                throw new CadastroException("O CNPJ informado já está cadastrado para outro cliente.");
            }

            clienteDAO.atualizar(cliente, conn);

        } catch (CadastroException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar cliente.", e);
            throw new ClienteException("Erro inesperado ao atualizar cliente.");
        }
    }

    public void inativar(Long id) throws ClienteException {
        try (Connection conn = ConexaoPool.getConexao()) {
            Cliente cliente = clienteDAO.buscarPorId(id, conn);
            if (cliente == null) {
                throw new ClienteException("Cliente não encontrado.");
            }

            cliente.setStatus(StatusCliente.INATIVO);
            clienteDAO.atualizar(cliente, conn);

        } catch (ClienteException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inativar cliente.", e);
            throw new ClienteException("Erro inesperado ao inativar cliente.");
        }
    }

    public void excluir(Long id) throws ClienteException {
        try (Connection conn = ConexaoPool.getConexao()) {
            Cliente cliente = clienteDAO.buscarPorId(id, conn);
            if (cliente == null) {
                throw new ClienteException("Cliente não encontrado.");
            }

            if (clienteDAO.possuiFretes(id, conn)) {
                throw new ClienteException(
                        "Não é permitido excluir um cliente que possui fretes cadastrados.");
            }

            clienteDAO.excluir(id, conn);

        } catch (ClienteException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao excluir cliente.", e);
            throw new ClienteException("Erro inesperado ao excluir cliente.");
        }
    }

    public Cliente buscarPorId(Long id) throws ClienteException {
        try (Connection conn = ConexaoPool.getConexao()) {
            Cliente cliente = clienteDAO.buscarPorId(id, conn);
            if (cliente == null) {
                throw new ClienteException("Cliente não encontrado.");
            }
            return cliente;

        } catch (ClienteException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar cliente.", e);
            throw new ClienteException("Erro inesperado ao buscar cliente.");
        }
    }

    public List<Cliente> listar(String filtro, int pagina, int itensPorPagina) throws ClienteException {
        try (Connection conn = ConexaoPool.getConexao()) {
            return clienteDAO.listar(filtro, pagina, itensPorPagina, conn);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar clientes.", e);
            throw new ClienteException("Erro inesperado ao listar clientes.");
        }
    }

    public int contarTotal(String filtro) throws ClienteException {
        try (Connection conn = ConexaoPool.getConexao()) {
            return clienteDAO.contarTotal(filtro, conn);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao contar clientes.", e);
            throw new ClienteException("Erro inesperado ao contar clientes.");
        }
    }

    // validações privadas

    private void validarCamposObrigatorios(Cliente cliente) throws CadastroException {
        if (cliente.getRazaoSocial() == null || cliente.getRazaoSocial().trim().isEmpty()) {
            throw new CadastroException("A razão social é obrigatória.");
        }
        if (cliente.getCnpj() == null || cliente.getCnpj().trim().isEmpty()) {
            throw new CadastroException("O CNPJ é obrigatório.");
        }
        if (cliente.getStatus() == null) {
            cliente.setStatus(StatusCliente.ATIVO);
        }
    }

    private void validarFormatos(Cliente cliente) throws CadastroException {

        String cnpj = cliente.getCnpj().replaceAll("[^0-9]", "");
        if (!CnpjUtil.validarCnpj(cnpj)) {
            throw new CadastroException("O CNPJ informado é inválido.");
        }
        cliente.setCnpj(cnpj);

        if (cliente.getCep() != null && !cliente.getCep().trim().isEmpty()) {
            if (!CepUtil.validar(cliente.getCep())) {
                throw new CadastroException("O CEP informado é inválido.");
            }
            cliente.setCep(CepUtil.limpar(cliente.getCep()));
        }

        if (cliente.getEmail() != null && !cliente.getEmail().trim().isEmpty()) {
            if (!EmailUtil.validar(cliente.getEmail())) {
                throw new CadastroException("O e-mail informado é inválido.");
            }
        }
    }
}