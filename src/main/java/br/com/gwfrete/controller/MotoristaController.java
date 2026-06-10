package br.com.gwfrete.controller;

import br.com.gwfrete.bo.MotoristaBO;
import br.com.gwfrete.exception.NegocioException;
import br.com.gwfrete.model.Motorista;
import br.com.gwfrete.model.CategoriaCnh;
import br.com.gwfrete.model.StatusMotorista;
import br.com.gwfrete.model.TipoVinculoMotorista;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MotoristaController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(MotoristaController.class.getName());
    private static final int ITENS_POR_PAGINA = 10;
    private final MotoristaBO motoristaBO = new MotoristaBO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();

        if (path == null || path.equals("/") || path.equals("/listar")) {
            listar(req, resp);
        } else if (path.equals("/novo")) {
            req.getRequestDispatcher("/WEB-INF/views/motorista/form.jsp").forward(req, resp);
        } else if (path.equals("/editar")) {
            editar(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/motoristas/listar");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();

        if (path == null) {
            resp.sendRedirect(req.getContextPath() + "/motoristas/listar");
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
            case "/suspender":
                suspender(req, resp);
                break;
            case "/excluir":
                excluir(req, resp);
                break;
            case "/ativar":
                ativar(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/motoristas/listar");
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

            List<Motorista> motoristas = motoristaBO.listar(filtro, pagina, ITENS_POR_PAGINA);
            int total = motoristaBO.contarTotal(filtro);
            int totalPaginas = (int) Math.ceil((double) total / ITENS_POR_PAGINA);

            req.setAttribute("motoristas", motoristas);
            req.setAttribute("filtro", filtro);
            req.setAttribute("paginaAtual", pagina);
            req.setAttribute("totalPaginas", totalPaginas);
            req.setAttribute("total", total);
            req.setAttribute("now", LocalDate.now());

            req.getRequestDispatcher("/WEB-INF/views/motorista/listar.jsp").forward(req, resp);

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("now", LocalDate.now());
            req.getRequestDispatcher("/WEB-INF/views/motorista/listar.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao listar motoristas.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void editar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            Motorista motorista = motoristaBO.buscarPorId(id);

            req.setAttribute("motorista", motorista);
            req.setAttribute("cnhVencida", motoristaBO.isCnhVencida(motorista));
            req.getRequestDispatcher("/WEB-INF/views/motorista/form.jsp").forward(req, resp);

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao buscar motorista para edição.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void salvar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Motorista motorista = extrairMotorista(req);
            motoristaBO.cadastrar(motorista);
            resp.sendRedirect(req.getContextPath() + "/motoristas/listar?sucesso=cadastrado");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("motorista", extrairMotoristaSemValidacao(req));
            req.getRequestDispatcher("/WEB-INF/views/motorista/form.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao cadastrar motorista.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void atualizar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Motorista motorista = extrairMotorista(req);
            motorista.setId(Long.parseLong(req.getParameter("id")));
            motorista.setStatus(StatusMotorista.fromString(req.getParameter("status")));
            motoristaBO.atualizar(motorista);
            resp.sendRedirect(req.getContextPath() + "/motoristas/listar?sucesso=atualizado");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("motorista", extrairMotoristaSemValidacao(req));
            req.getRequestDispatcher("/WEB-INF/views/motorista/form.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar motorista.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void inativar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            motoristaBO.inativar(id);
            resp.sendRedirect(req.getContextPath() + "/motoristas/listar?sucesso=inativado");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao inativar motorista.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void ativar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            motoristaBO.ativar(id);
            resp.sendRedirect(req.getContextPath() + "/motoristas/listar?sucesso=ativado");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao ativar motorista.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void suspender(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            motoristaBO.suspender(id);
            resp.sendRedirect(req.getContextPath() + "/motoristas/listar?sucesso=suspenso");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao suspender motorista.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void excluir(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            motoristaBO.excluir(id);
            resp.sendRedirect(req.getContextPath() + "/motoristas/listar?sucesso=excluido");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao excluir motorista.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private Motorista extrairMotorista(HttpServletRequest req) {
        Motorista motorista = new Motorista();
        motorista.setNome(req.getParameter("nome"));
        motorista.setCpf(req.getParameter("cpf"));
        motorista.setTelefone(req.getParameter("telefone"));
        motorista.setNumeroCnh(req.getParameter("numeroCnh"));

        String dataNascimento = req.getParameter("dataNascimento");
        if (dataNascimento != null && !dataNascimento.isEmpty()) {
            motorista.setDataNascimento(LocalDate.parse(dataNascimento));
        }

        String dataValidadeCnh = req.getParameter("dataValidadeCnh");
        if (dataValidadeCnh != null && !dataValidadeCnh.isEmpty()) {
            motorista.setDataValidadeCnh(LocalDate.parse(dataValidadeCnh));
        }

        String categoriaCnh = req.getParameter("categoriaCnh");
        if (categoriaCnh != null && !categoriaCnh.isEmpty()) {
            motorista.setCategoriaCnh(CategoriaCnh.fromString(categoriaCnh));
        }

        String tipoVinculo = req.getParameter("tipoVinculo");
        if (tipoVinculo != null && !tipoVinculo.isEmpty()) {
            motorista.setTipoVinculo(TipoVinculoMotorista.fromString(tipoVinculo));
        }

        return motorista;
    }

    private Motorista extrairMotoristaSemValidacao(HttpServletRequest req) {
        Motorista motorista = extrairMotorista(req);
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                motorista.setId(Long.parseLong(idParam));
            } catch (NumberFormatException e) {
            }
        }
        String statusParam = req.getParameter("status");
        if (statusParam != null && !statusParam.isEmpty()) {
            try {
                motorista.setStatus(StatusMotorista.fromString(statusParam));
            } catch (Exception e) {
            }
        }
        return motorista;
    }
}