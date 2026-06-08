package br.com.gwfrete.controller;

import br.com.gwfrete.bo.FreteBO;
import br.com.gwfrete.bo.OcorrenciaFreteBO;
import br.com.gwfrete.exception.NegocioException;
import br.com.gwfrete.model.Frete;
import br.com.gwfrete.model.OcorrenciaFrete;
import br.com.gwfrete.model.TipoOcorrencia;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OcorrenciaFreteController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(OcorrenciaFreteController.class.getName());
    private final OcorrenciaFreteBO ocorrenciaBO = new OcorrenciaFreteBO();
    private final FreteBO freteBO = new FreteBO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();

        if (path == null || path.equals("/") || path.equals("/listar")) {
            listar(req, resp);
        } else if (path.equals("/novo")) {
            abrirFormNovo(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/fretes/listar");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();

        if (path == null) {
            resp.sendRedirect(req.getContextPath() + "/fretes/listar");
            return;
        }

        switch (path) {
            case "/novo":
                registrar(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/fretes/listar");
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long idFrete = Long.parseLong(req.getParameter("idFrete"));
            Frete frete = freteBO.buscarPorId(idFrete);
            List<OcorrenciaFrete> ocorrencias = ocorrenciaBO.listarPorFrete(idFrete);

            req.setAttribute("frete", frete);
            req.setAttribute("ocorrencias", ocorrencias);
            req.getRequestDispatcher("/WEB-INF/views/ocorrencia/lista.jsp").forward(req, resp);

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/ocorrencia/lista.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao listar ocorrências.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void abrirFormNovo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long idFrete = Long.parseLong(req.getParameter("idFrete"));
            Frete frete = freteBO.buscarPorId(idFrete);

            req.setAttribute("frete", frete);
            req.setAttribute("tiposOcorrencia", TipoOcorrencia.values());
            req.getRequestDispatcher("/WEB-INF/views/ocorrencia/form.jsp").forward(req, resp);

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/fretes/listar");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao abrir formulário de ocorrência.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void registrar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            OcorrenciaFrete ocorrencia = extrairOcorrencia(req);
            ocorrenciaBO.registrar(ocorrencia);
            Long idFrete = ocorrencia.getFrete().getId();
            resp.sendRedirect(req.getContextPath() + "/ocorrencias/listar?idFrete=" + idFrete + "&sucesso=registrado");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            try {
                Long idFrete = Long.parseLong(req.getParameter("idFrete"));
                Frete frete = freteBO.buscarPorId(idFrete);
                req.setAttribute("frete", frete);
                req.setAttribute("tiposOcorrencia", TipoOcorrencia.values());
                req.setAttribute("ocorrencia", extrairOcorrenciaSemValidacao(req));
            } catch (Exception ex) {
            }
            req.getRequestDispatcher("/WEB-INF/views/ocorrencia/form.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao registrar ocorrência.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private OcorrenciaFrete extrairOcorrencia(HttpServletRequest req) {
        OcorrenciaFrete ocorrencia = new OcorrenciaFrete();

        String idFreteParam = req.getParameter("idFrete");
        if (idFreteParam != null && !idFreteParam.isEmpty()) {
            Frete frete = new Frete();
            try {
                frete.setId(Long.parseLong(idFreteParam));
            } catch (NumberFormatException e) {
            }
            ocorrencia.setFrete(frete);
        }

        String tipoParam = req.getParameter("tipo");
        if (tipoParam != null && !tipoParam.isEmpty()) {
            try {
                ocorrencia.setTipo(TipoOcorrencia.fromString(tipoParam));
            } catch (Exception e) {
            }
        }

        String dataHoraParam = req.getParameter("dataHoraOcorrencia");
        if (dataHoraParam != null && !dataHoraParam.isEmpty()) {
            try {
                ocorrencia.setDataHoraOcorrencia(LocalDateTime.parse(dataHoraParam));
            } catch (Exception e) {
            }
        }

        ocorrencia.setMunicipio(req.getParameter("municipio"));
        ocorrencia.setUf(req.getParameter("uf"));
        ocorrencia.setDescricao(req.getParameter("descricao"));
        ocorrencia.setNomeRecebedor(req.getParameter("nomeRecebedor"));
        ocorrencia.setDocumentoRecebedor(req.getParameter("documentoRecebedor"));

        return ocorrencia;
    }

    private OcorrenciaFrete extrairOcorrenciaSemValidacao(HttpServletRequest req) {
        return extrairOcorrencia(req);
    }
}