package br.com.gwfrete.controller;

import br.com.gwfrete.bo.ClienteBO;
import br.com.gwfrete.exception.NegocioException;
import br.com.gwfrete.model.Cliente;
import br.com.gwfrete.model.StatusCliente;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ClienteController.class.getName());
    private static final int ITENS_POR_PAGINA = 10;
    private final ClienteBO clienteBO = new ClienteBO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();

        if (path == null || path.equals("/") || path.equals("/listar")) {
            listar(req, resp);
        } else if (path.equals("/novo")) {
            req.getRequestDispatcher("/WEB-INF/views/cliente/form.jsp").forward(req, resp);
        } else if (path.equals("/editar")) {
            editar(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/cliente/listar");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();

        if (path == null) {
            resp.sendRedirect(req.getContextPath() + "/cliente/listar");
            return;
        }

        switch (path) {
            case "/novo":
                salvar(req, resp);
                break;
            case "/editar":
                atualizar(req, resp);
                break;
            case "/inativar":
                inativar(req, resp);
                break;
            case "/excluir":
                excluir(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/cliente/listar");
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String filtro = req.getParameter("filtro");
            int pagina = 1;
            if (req.getParameter("pagina") != null) {
                try {
                    pagina = Integer.parseInt(req.getParameter("pagina"));
                } catch (NumberFormatException e) {
                    pagina = 1;
                }
            }

            List<Cliente> clientes = clienteBO.listar(filtro, pagina, ITENS_POR_PAGINA);
            int total = clienteBO.contarTotal(filtro);
            int totalPaginas = (int) Math.ceil((double) total / ITENS_POR_PAGINA);

            req.setAttribute("clientes", clientes);
            req.setAttribute("filtro", filtro);
            req.setAttribute("paginaAtual", pagina);
            req.setAttribute("totalPaginas", totalPaginas);
            req.setAttribute("total", total);

            req.getRequestDispatcher("/WEB-INF/views/cliente/listar.jsp").forward(req, resp);

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/cliente/listar.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao listar clientes.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void editar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            Cliente cliente = clienteBO.buscarPorId(id);

            req.setAttribute("cliente", cliente);
            req.getRequestDispatcher("/WEB-INF/views/cliente/form.jsp").forward(req, resp);

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/cliente/listar.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao buscar cliente para edição.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void salvar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Cliente cliente = extrairCliente(req);
            clienteBO.cadastrar(cliente);
            resp.sendRedirect(req.getContextPath() + "/cliente/listar?sucesso=cadastrado");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("cliente", extrairClienteSemValidacao(req));
            req.getRequestDispatcher("/WEB-INF/views/cliente/form.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao cadastrar cliente.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void atualizar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Cliente cliente = extrairCliente(req);
            cliente.setId(Long.parseLong(req.getParameter("id")));
            cliente.setStatus(StatusCliente.fromString(req.getParameter("status")));
            clienteBO.atualizar(cliente);
            resp.sendRedirect(req.getContextPath() + "/cliente/listar?sucesso=atualizado");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("cliente", extrairClienteSemValidacao(req));
            req.getRequestDispatcher("/WEB-INF/views/cliente/form.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar cliente.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void inativar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            clienteBO.inativar(id);
            resp.sendRedirect(req.getContextPath() + "/cliente/listar?sucesso=inativado");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao inativar cliente.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void excluir(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            clienteBO.excluir(id);
            resp.sendRedirect(req.getContextPath() + "/cliente/listar?sucesso=excluido");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao excluir cliente.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private Cliente extrairCliente(HttpServletRequest req) {
        Cliente cliente = new Cliente();
        cliente.setRazaoSocial(req.getParameter("razaoSocial"));
        cliente.setNomeFantasia(req.getParameter("nomeFantasia"));
        cliente.setCnpj(req.getParameter("cnpj"));
        cliente.setInscricaoEstadual(req.getParameter("inscricaoEstadual"));
        cliente.setLogradouro(req.getParameter("logradouro"));
        cliente.setNumero(req.getParameter("numero"));
        cliente.setComplemento(req.getParameter("complemento"));
        cliente.setBairro(req.getParameter("bairro"));
        cliente.setMunicipio(req.getParameter("municipio"));
        cliente.setUf(req.getParameter("uf"));
        cliente.setCep(req.getParameter("cep"));
        cliente.setTelefone(req.getParameter("telefone"));
        cliente.setEmail(req.getParameter("email"));
        return cliente;
    }

    private Cliente extrairClienteSemValidacao(HttpServletRequest req) {
        Cliente cliente = extrairCliente(req);
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                cliente.setId(Long.parseLong(idParam));
            } catch (NumberFormatException e) {
                LOGGER.log(Level.SEVERE, "Erro ao converter ID do cliente.", e);
            }
        }
        String statusParam = req.getParameter("status");
        if (statusParam != null && !statusParam.isEmpty()) {
            try {
                cliente.setStatus(StatusCliente.fromString(statusParam));
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro ao converter status do cliente.", e);
            }
        }
        return cliente;
    }
}