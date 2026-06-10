package br.com.gwfrete.controller;

import br.com.gwfrete.bo.VeiculoBO;
import br.com.gwfrete.exception.NegocioException;
import br.com.gwfrete.model.TipoVeiculo;
import br.com.gwfrete.model.StatusVeiculo;
import br.com.gwfrete.model.Veiculo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VeiculoController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(VeiculoController.class.getName());
    private static final int ITENS_POR_PAGINA = 10;
    private final VeiculoBO veiculoBO = new VeiculoBO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();

        if (path == null || path.equals("/") || path.equals("/listar")) {
            listar(req, resp);
        } else if (path.equals("/novo")) {
            req.getRequestDispatcher("/WEB-INF/views/veiculo/form.jsp").forward(req, resp);
        } else if (path.equals("/editar")) {
            editar(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/veiculos/listar");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();

        if (path == null) {
            resp.sendRedirect(req.getContextPath() + "/veiculos/listar");
            return;
        }

        switch (path) {
            case "/novo":
                salvar(req, resp);
                break;
            case "/editar":
                atualizar(req, resp);
                break;
            case "/disponivel":
                alterarParaDisponivel(req, resp);
                break;
            // case "/manutencao":
            //     alterarParaManutencao(req, resp);
            //     break;
            case "/excluir":
                excluir(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/veiculos/listar");
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

            List<Veiculo> veiculos = veiculoBO.listar(filtro, pagina, ITENS_POR_PAGINA);
            int total = veiculoBO.contarTotal(filtro);
            int totalPaginas = (int) Math.ceil((double) total / ITENS_POR_PAGINA);

            req.setAttribute("veiculos", veiculos);
            req.setAttribute("filtro", filtro);
            req.setAttribute("paginaAtual", pagina);
            req.setAttribute("totalPaginas", totalPaginas);
            req.setAttribute("total", total);

            req.getRequestDispatcher("/WEB-INF/views/veiculo/listar.jsp").forward(req, resp);

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/veiculo/listar.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao listar veículos.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void editar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            Veiculo veiculo = veiculoBO.buscarPorId(id);

            req.setAttribute("veiculo", veiculo);
            req.getRequestDispatcher("/WEB-INF/views/veiculo/form.jsp").forward(req, resp);

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao buscar veículo para edição.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void salvar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Veiculo veiculo = extrairVeiculo(req);
            veiculoBO.cadastrar(veiculo);
            resp.sendRedirect(req.getContextPath() + "/veiculos/listar?sucesso=cadastrado");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("veiculo", extrairVeiculoSemValidacao(req));
            req.getRequestDispatcher("/WEB-INF/views/veiculo/form.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao cadastrar veículo.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void atualizar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Veiculo veiculo = extrairVeiculo(req);
            veiculo.setId(Long.parseLong(req.getParameter("id")));
            veiculo.setStatus(StatusVeiculo.fromString(req.getParameter("status")));
            veiculoBO.atualizar(veiculo);
            resp.sendRedirect(req.getContextPath() + "/veiculos/listar?sucesso=atualizado");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("veiculo", extrairVeiculoSemValidacao(req));
            req.getRequestDispatcher("/WEB-INF/views/veiculo/form.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar veículo.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void alterarParaDisponivel(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            veiculoBO.alterarStatusParaDisponivel(id);
            resp.sendRedirect(req.getContextPath() + "/veiculos/listar?sucesso=disponivel");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao alterar status do veículo.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    // private void alterarParaManutencao(HttpServletRequest req, HttpServletResponse resp)
    //         throws ServletException, IOException {
    //     try {
    //         Long id = Long.parseLong(req.getParameter("id"));
    //         veiculoBO.alterarStatusParaManutencao(id);
    //         resp.sendRedirect(req.getContextPath() + "/veiculos/listar?sucesso=manutencao");

    //     } catch (NegocioException e) {
    //         req.setAttribute("erro", e.getMessage());
    //         listar(req, resp);
    //     } catch (Exception e) {
    //         LOGGER.log(Level.SEVERE, "Erro inesperado ao alterar status do veículo para manutenção.", e);
    //         resp.sendRedirect(req.getContextPath() + "/erro");
    //     }
    // }

    private void excluir(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            veiculoBO.excluir(id);
            resp.sendRedirect(req.getContextPath() + "/veiculos/listar?sucesso=excluido");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao excluir veículo.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private Veiculo extrairVeiculo(HttpServletRequest req) {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(req.getParameter("placa"));
        veiculo.setRntrc(req.getParameter("rntrc"));

        String anoFabricacao = req.getParameter("anoFabricacao");
        if (anoFabricacao != null && !anoFabricacao.isEmpty()) {
            try {
                veiculo.setAnoFabricacao(Integer.parseInt(anoFabricacao));
            } catch (NumberFormatException e) {
            }
        }

        String tipoVeiculo = req.getParameter("tipoVeiculo");
        if (tipoVeiculo != null && !tipoVeiculo.isEmpty()) {
            try {
                veiculo.setTipoVeiculo(TipoVeiculo.fromString(tipoVeiculo));
            } catch (Exception e) {
            }
        }

        String tara = req.getParameter("tara");
        if (tara != null && !tara.isEmpty()) {
            try {
                veiculo.setTara(Double.parseDouble(tara.replace(',', '.')));
            } catch (NumberFormatException e) {
            }
        }

        String capacidade = req.getParameter("capacidadeCarga");
        if (capacidade != null && !capacidade.isEmpty()) {
            try {
                veiculo.setCapacidadeCarga(Double.parseDouble(capacidade.replace(',', '.')));
            } catch (NumberFormatException e) {
            }
        }

        String volume = req.getParameter("volume");
        if (volume != null && !volume.isEmpty()) {
            try {
                veiculo.setVolume(Double.parseDouble(volume.replace(',', '.')));
            } catch (NumberFormatException e) {
            }
        }

        return veiculo;
    }

    private Veiculo extrairVeiculoSemValidacao(HttpServletRequest req) {
        Veiculo veiculo = extrairVeiculo(req);
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                veiculo.setId(Long.parseLong(idParam));
            } catch (NumberFormatException e) {
            }
        }
        String statusParam = req.getParameter("status");
        if (statusParam != null && !statusParam.isEmpty()) {
            try {
                veiculo.setStatus(StatusVeiculo.fromString(statusParam));
            } catch (Exception e) {
            }
        }
        return veiculo;
    }
}