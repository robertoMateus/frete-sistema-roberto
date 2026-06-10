package br.com.gwfrete.controller;

import br.com.gwfrete.bo.ManutencaoVeiculoBO;
import br.com.gwfrete.bo.VeiculoBO;
import br.com.gwfrete.exception.NegocioException;
import br.com.gwfrete.model.ManutencaoVeiculo;
import br.com.gwfrete.model.TipoManutencao;
import br.com.gwfrete.model.Veiculo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManutencaoVeiculoController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ManutencaoVeiculoController.class.getName());
    private final ManutencaoVeiculoBO manutencaoBO = new ManutencaoVeiculoBO();
    private final VeiculoBO veiculoBO = new VeiculoBO();
    private static final int ITENS_POR_PAGINA = 10;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();

        if (path == null || path.equals("/") || path.equals("/listar")) {
            listar(req, resp);
        } else if (path.equals("/novo")) {
            abrirFormNovo(req, resp);
        } else if (path.equals("/editar")) {
            editar(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/manutencoes/listar");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();

        if (path == null) {
            resp.sendRedirect(req.getContextPath() + "/manutencoes/listar");
            return;
        }

        switch (path) {
            case "/novo":
                registrar(req, resp);
                break;
            case "/editar":
                atualizar(req, resp);
                break;
            case "/concluir":
                concluir(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/manutencoes/listar");
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int pagina = 1;
            if (req.getParameter("pagina") != null) {
                try {
                    pagina = Integer.parseInt(req.getParameter("pagina"));
                } catch (NumberFormatException e) {
                    pagina = 1;
                }
            }
            String idVeiculoParam = req.getParameter("idVeiculo");
            if (idVeiculoParam != null && !idVeiculoParam.isEmpty()) {
                Long idVeiculo = Long.parseLong(idVeiculoParam);
                Veiculo veiculo = veiculoBO.buscarPorId(idVeiculo);
                List<ManutencaoVeiculo> manutencoes = manutencaoBO.listarPorVeiculo(idVeiculo, pagina,
                        ITENS_POR_PAGINA);
                int total = manutencaoBO.contarPorVeiculo(idVeiculo);
                int totalPaginas = (int) Math.ceil((double) total / ITENS_POR_PAGINA);
                req.setAttribute("veiculo", veiculo);
                req.setAttribute("manutencoes", manutencoes);
                req.setAttribute("total", total);
                req.setAttribute("totalPaginas", totalPaginas);
            } else {
                List<ManutencaoVeiculo> manutencoes = manutencaoBO.listarEmAberto(pagina, ITENS_POR_PAGINA);
                int total = manutencaoBO.contarEmAberto();
                int totalPaginas = (int) Math.ceil((double) total / ITENS_POR_PAGINA);
                req.setAttribute("manutencoes", manutencoes);
                req.setAttribute("total", total);
                req.setAttribute("totalPaginas", totalPaginas);
            }
            req.setAttribute("paginaAtual", pagina);
            req.getRequestDispatcher("/WEB-INF/views/manutencoes/listar.jsp").forward(req, resp);
        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/manutencoes/listar.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao listar manutenções.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void abrirFormNovo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String idVeiculoParam = req.getParameter("idVeiculo");
            if (idVeiculoParam != null && !idVeiculoParam.isEmpty()) {
                Veiculo veiculo = veiculoBO.buscarPorId(Long.parseLong(idVeiculoParam));
                req.setAttribute("veiculo", veiculo);
            } else {
                req.setAttribute("veiculos", veiculoBO.listar(null, 1, Integer.MAX_VALUE));
            }

            req.setAttribute("tiposManutencao", TipoManutencao.values());
            req.getRequestDispatcher("/WEB-INF/views/manutencoes/form.jsp").forward(req, resp);

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/manutencoes/listar");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao abrir formulário de manutenção.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void editar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            ManutencaoVeiculo manutencao = manutencaoBO.buscarPorId(id);

            req.setAttribute("manutencao", manutencao);
            req.setAttribute("tiposManutencao", TipoManutencao.values());
            req.getRequestDispatcher("/WEB-INF/views/manutencoes/form.jsp").forward(req, resp);

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao buscar manutenção para edição.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void registrar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            ManutencaoVeiculo manutencao = extrairManutencao(req);
            manutencaoBO.registrar(manutencao);
            Long idVeiculo = manutencao.getVeiculo().getId();
            resp.sendRedirect(
                    req.getContextPath() + "/manutencoes/listar?idVeiculo=" + idVeiculo + "&sucesso=registrado");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("manutencao", extrairManutencaoSemValidacao(req));
            req.setAttribute("tiposManutencao", TipoManutencao.values());
            try {
                String idVeiculoParam = req.getParameter("idVeiculo");
                if (idVeiculoParam != null && !idVeiculoParam.isEmpty()) {
                    req.setAttribute("veiculo", veiculoBO.buscarPorId(Long.parseLong(idVeiculoParam)));
                }
            } catch (NegocioException ex) {
            }
            req.getRequestDispatcher("/WEB-INF/views/manutencoes/form.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao registrar manutenção.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void atualizar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            ManutencaoVeiculo manutencao = extrairManutencao(req);
            manutencao.setId(Long.parseLong(req.getParameter("id")));
            manutencaoBO.atualizar(manutencao);
            Long idVeiculo = manutencao.getVeiculo().getId();
            resp.sendRedirect(
                    req.getContextPath() + "/manutencoes/listar?idVeiculo=" + idVeiculo + "&sucesso=atualizado");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("manutencao", extrairManutencaoSemValidacao(req));
            req.setAttribute("tiposManutencao", TipoManutencao.values());
            req.getRequestDispatcher("/WEB-INF/views/manutencoes/form.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar manutenção.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void concluir(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            manutencaoBO.concluir(id);
            resp.sendRedirect(req.getContextPath() + "/manutencoes/listar?sucesso=concluido");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao concluir manutenção.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private ManutencaoVeiculo extrairManutencao(HttpServletRequest req) {
        ManutencaoVeiculo manutencao = new ManutencaoVeiculo();

        String idVeiculoParam = req.getParameter("idVeiculo");
        if (idVeiculoParam != null && !idVeiculoParam.isEmpty()) {
            Veiculo veiculo = new Veiculo();
            try {
                veiculo.setId(Long.parseLong(idVeiculoParam));
            } catch (NumberFormatException e) {
            }
            manutencao.setVeiculo(veiculo);
        }

        String tipoParam = req.getParameter("tipo");
        if (tipoParam != null && !tipoParam.isEmpty()) {
            try {
                manutencao.setTipo(TipoManutencao.fromString(tipoParam));
            } catch (Exception e) {
            }
        }

        manutencao.setDescricao(req.getParameter("descricao"));

        String dataInicio = req.getParameter("dataInicio");
        if (dataInicio != null && !dataInicio.isEmpty()) {
            try {
                manutencao.setDataInicio(LocalDate.parse(dataInicio));
            } catch (Exception e) {
            }
        }

        String dataFim = req.getParameter("dataFim");
        if (dataFim != null && !dataFim.isEmpty()) {
            try {
                manutencao.setDataFim(LocalDate.parse(dataFim));
            } catch (Exception e) {
            }
        }

        String custo = req.getParameter("custo");
        if (custo != null && !custo.isEmpty()) {
            try {
                manutencao.setCusto(new BigDecimal(custo));
            } catch (NumberFormatException e) {
            }
        }

        return manutencao;
    }

    private ManutencaoVeiculo extrairManutencaoSemValidacao(HttpServletRequest req) {
        ManutencaoVeiculo manutencao = extrairManutencao(req);
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                manutencao.setId(Long.parseLong(idParam));
            } catch (NumberFormatException e) {
            }
        }
        return manutencao;
    }
}