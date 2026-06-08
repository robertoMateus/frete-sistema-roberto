package br.com.gwfrete.controller;

import br.com.gwfrete.bo.FreteBO;
import br.com.gwfrete.bo.ManutencaoVeiculoBO;
import br.com.gwfrete.exception.NegocioException;
import br.com.gwfrete.model.Frete;
import br.com.gwfrete.model.ManutencaoVeiculo;
import br.com.gwfrete.model.StatusFrete;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(HomeController.class.getName());
    private final FreteBO freteBO = new FreteBO();
    private final ManutencaoVeiculoBO manutencaoBO = new ManutencaoVeiculoBO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int totalEmitidos = freteBO.contarPorStatus(StatusFrete.EMITIDO);
            int totalSaidaConfirmada = freteBO.contarPorStatus(StatusFrete.SAIDA_CONFIRMADA);
            int totalEmTransito = freteBO.contarPorStatus(StatusFrete.EM_TRANSITO);
            int totalAtrasados = freteBO.contarAtrasados();

            List<Frete> ultimosFretes = freteBO.listarEmAberto();
            if (ultimosFretes.size() > 5) {
                ultimosFretes = ultimosFretes.subList(0, 5);
            }

            List<ManutencaoVeiculo> manutencoesEmAberto = manutencaoBO.listarEmAberto();

            req.setAttribute("totalEmitidos", totalEmitidos);
            req.setAttribute("totalSaidaConfirmada", totalSaidaConfirmada);
            req.setAttribute("totalEmTransito", totalEmTransito);
            req.setAttribute("totalAtrasados", totalAtrasados);
            req.setAttribute("ultimosFretes", ultimosFretes);
            req.setAttribute("manutencoesEmAberto", manutencoesEmAberto);

            req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao carregar dashboard.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }
}