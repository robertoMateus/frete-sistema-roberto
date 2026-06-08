package br.com.gwfrete.controller;

import br.com.gwfrete.bo.PrecoRotaBO;
import br.com.gwfrete.exception.NegocioException;
import br.com.gwfrete.model.PrecoRota;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrecoRotaController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(PrecoRotaController.class.getName());
    private static final int ITENS_POR_PAGINA = 10;
    private final PrecoRotaBO precoRotaBO = new PrecoRotaBO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();

        if (path == null || path.equals("/") || path.equals("/listar")) {
            listar(req, resp);
        } else if (path.equals("/novo")) {
            req.getRequestDispatcher("/WEB-INF/views/precoRota/form.jsp").forward(req, resp);
        } else if (path.equals("/editar")) {
            editar(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/precosRota/listar");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();

        if (path == null) {
            resp.sendRedirect(req.getContextPath() + "/precosRota/listar");
            return;
        }

        switch (path) {
            case "/novo":
                salvar(req, resp);
                break;
            case "/editar":
                atualizar(req, resp);
                break;
            case "/excluir":
                excluir(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/precosRota/listar");
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

            List<PrecoRota> rotas = precoRotaBO.listar(filtro, pagina, ITENS_POR_PAGINA);
            int total = precoRotaBO.contarTotal(filtro);
            int totalPaginas = (int) Math.ceil((double) total / ITENS_POR_PAGINA);

            req.setAttribute("rotas", rotas);
            req.setAttribute("filtro", filtro);
            req.setAttribute("paginaAtual", pagina);
            req.setAttribute("totalPaginas", totalPaginas);
            req.setAttribute("total", total);

            req.getRequestDispatcher("/WEB-INF/views/precoRota/lista.jsp").forward(req, resp);

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/precoRota/lista.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao listar preços de rota.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void editar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            PrecoRota precoRota = precoRotaBO.buscarPorId(id);

            req.setAttribute("precoRota", precoRota);
            req.getRequestDispatcher("/WEB-INF/views/precoRota/form.jsp").forward(req, resp);

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao buscar preço de rota para edição.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void salvar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            PrecoRota precoRota = extrairPrecoRota(req);
            precoRotaBO.cadastrar(precoRota);
            resp.sendRedirect(req.getContextPath() + "/precosRota/listar?sucesso=cadastrado");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("precoRota", extrairPrecoRotaSemValidacao(req));
            req.getRequestDispatcher("/WEB-INF/views/precoRota/form.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao cadastrar preço de rota.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void atualizar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            PrecoRota precoRota = extrairPrecoRota(req);
            precoRota.setId(Long.parseLong(req.getParameter("id")));
            precoRotaBO.atualizar(precoRota);
            resp.sendRedirect(req.getContextPath() + "/precosRota/listar?sucesso=atualizado");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("precoRota", extrairPrecoRotaSemValidacao(req));
            req.getRequestDispatcher("/WEB-INF/views/precoRota/form.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar preço de rota.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void excluir(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            precoRotaBO.excluir(id);
            resp.sendRedirect(req.getContextPath() + "/precosRota/listar?sucesso=excluido");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao excluir preço de rota.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private PrecoRota extrairPrecoRota(HttpServletRequest req) {
        PrecoRota precoRota = new PrecoRota();

        precoRota.setMunicipioOrigem(req.getParameter("municipioOrigem"));
        precoRota.setUfOrigem(req.getParameter("ufOrigem"));
        precoRota.setMunicipioDestino(req.getParameter("municipioDestino"));
        precoRota.setUfDestino(req.getParameter("ufDestino"));

        String valorBase = req.getParameter("valorBase");
        if (valorBase != null && !valorBase.isEmpty()) {
            try {
                precoRota.setValorBase(new BigDecimal(valorBase.replace(",", ".")));
            } catch (NumberFormatException e) {
            }
        }

        String valorPorKg = req.getParameter("valorPorKg");
        if (valorPorKg != null && !valorPorKg.isEmpty()) {
            try {
                precoRota.setValorPorKg(new BigDecimal(valorPorKg.replace(",", ".")));
            } catch (NumberFormatException e) {
            }
        }

        return precoRota;
    }

    private PrecoRota extrairPrecoRotaSemValidacao(HttpServletRequest req) {
        PrecoRota precoRota = extrairPrecoRota(req);
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                precoRota.setId(Long.parseLong(idParam));
            } catch (NumberFormatException e) {
            }
        }
        return precoRota;
    }
}