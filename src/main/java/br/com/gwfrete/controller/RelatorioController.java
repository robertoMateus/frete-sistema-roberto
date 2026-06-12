package br.com.gwfrete.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.gwfrete.bo.RelatorioBO;
import br.com.gwfrete.exception.NegocioException;
import br.com.gwfrete.util.ConexaoPool;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RelatorioController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RelatorioController.class.getName());
    private JasperReport relatorioFreteAberto;
    private JasperReport relatorioRomaneio;


    @Override
    public void init() throws ServletException {
        try {
            String base = getServletContext().getRealPath("/WEB-INF/reports/");
            relatorioFreteAberto = JasperCompileManager.compileReport(base + "fretes_em_aberto.jrxml");
            relatorioRomaneio = JasperCompileManager.compileReport(base + "romaneio.jrxml");
        } catch (JRException e) {
            throw new ServletException("Erro ao compilar relatórios.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();

        if ("/fretes-em-aberto".equals(path)) {
            gerarFretesEmAberto(req, resp);
        } else if ("/romaneio".equals(path)) {
            gerarRomaneio(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/home");
        }
    }

    private void gerarFretesEmAberto(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Connection conn = null;
        try {
            conn = ConexaoPool.getConexao();

            Map<String, Object> params = new HashMap<>();
            params.put("REPORT_TITLE", "Fretes em Aberto");
            params.put("DATA_GERACAO", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));

            JasperPrint jasperPrint = JasperFillManager.fillReport(relatorioFreteAberto, params, conn);

            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "inline; filename=fretes_em_aberto.pdf");
            JasperExportManager.exportReportToPdfStream(jasperPrint, resp.getOutputStream());

        } catch (JRException e) {
            LOGGER.log(Level.SEVERE, "Erro ao gerar relatório de fretes em aberto.", e);
            req.setAttribute("erro", "Erro ao gerar o relatório. Tente novamente.");
            req.getRequestDispatcher("/WEB-INF/views/erro.jsp").forward(req, resp);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de banco ao gerar relatório de fretes em aberto.", e);
            req.setAttribute("erro", "Erro ao acessar os dados. Tente novamente.");
            req.getRequestDispatcher("/WEB-INF/views/erro.jsp").forward(req, resp);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
        }
    }

    private void gerarRomaneio(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idMotoristaStr = req.getParameter("idMotorista");
        String dataStr = req.getParameter("data");

        Connection conn = null;
        try {
            long idMotorista = Long.parseLong(idMotoristaStr);
            Date dataRomaneio = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(dataStr).getTime());

            RelatorioBO bo = new RelatorioBO();
            Map<String, Object> params = bo.montarParametrosRomaneio(idMotorista, dataRomaneio);

            conn = ConexaoPool.getConexao();

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    relatorioRomaneio, params, conn);

            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "inline; filename=romaneio.pdf");
            JasperExportManager.exportReportToPdfStream(jasperPrint, resp.getOutputStream());

        } catch (NegocioException e) {
            System.out.println("NEGOCIO EXCEPTION: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println(
                    "<span id=\"mensagem-erro-romaneio\">" + e.getMessage() + "</span>");

        } catch (NumberFormatException | ParseException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println(
                    "<span id=\"mensagem-erro-romaneio\">Parâmetros inválidos: informe um motorista e uma data válidos.</span>");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao gerar romaneio.", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println(
                    "<span id=\"mensagem-erro-romaneio\">Erro ao gerar o romaneio. Tente novamente.</span>");

        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
        }
    }
}