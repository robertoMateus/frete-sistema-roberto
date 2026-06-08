package br.com.gwfrete.controller;

import br.com.gwfrete.bo.ClienteBO;
import br.com.gwfrete.bo.FreteBO;
import br.com.gwfrete.bo.MotoristaBO;
import br.com.gwfrete.bo.VeiculoBO;
import br.com.gwfrete.exception.NegocioException;
import br.com.gwfrete.model.Cliente;
import br.com.gwfrete.model.Frete;
import br.com.gwfrete.model.Motorista;
import br.com.gwfrete.model.Veiculo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FreteController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(FreteController.class.getName());
    private static final int ITENS_POR_PAGINA = 10;
    private final FreteBO freteBO = new FreteBO();
    private final ClienteBO clienteBO = new ClienteBO();
    private final MotoristaBO motoristaBO = new MotoristaBO();
    private final VeiculoBO veiculoBO = new VeiculoBO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();

        if (path == null || path.equals("/") || path.equals("/listar")) {
            listar(req, resp);
        } else if (path.equals("/novo")) {
            abrirFormNovo(req, resp);
        } else if (path.equals("/detalhe")) {
            detalhe(req, resp);
        } else if (path.equals("/confirmarSaida")) {
            abrirConfirmarSaida(req, resp);
        } else if (path.equals("/naoEntregue")) {
            abrirNaoEntregue(req, resp);
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
                emitir(req, resp);
                break;
            case "/confirmarSaida":
                confirmarSaida(req, resp);
                break;
            case "/emTransito":
                registrarEmTransito(req, resp);
                break;
            case "/entregue":
                registrarEntrega(req, resp);
                break;
            case "/naoEntregue":
                registrarNaoEntregue(req, resp);
                break;
            case "/cancelar":
                cancelar(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/fretes/listar");
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

            List<Frete> fretes = freteBO.listar(filtro, pagina, ITENS_POR_PAGINA);
            int total = freteBO.contarTotal(filtro);
            int totalPaginas = (int) Math.ceil((double) total / ITENS_POR_PAGINA);

            req.setAttribute("fretes", fretes);
            req.setAttribute("filtro", filtro);
            req.setAttribute("paginaAtual", pagina);
            req.setAttribute("totalPaginas", totalPaginas);
            req.setAttribute("total", total);

            req.getRequestDispatcher("/WEB-INF/views/frete/lista.jsp").forward(req, resp);

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/frete/lista.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao listar fretes.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void abrirFormNovo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("clientes", clienteBO.listar(null, 1, Integer.MAX_VALUE));
            req.setAttribute("motoristas", motoristaBO.listar(null, 1, Integer.MAX_VALUE));
            req.setAttribute("veiculos", veiculoBO.listar(null, 1, Integer.MAX_VALUE));
            req.getRequestDispatcher("/WEB-INF/views/frete/form.jsp").forward(req, resp);

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/frete/form.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao abrir formulário de frete.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void detalhe(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            Frete frete = freteBO.buscarPorId(id);

            req.setAttribute("frete", frete);
            req.getRequestDispatcher("/WEB-INF/views/frete/detalhe.jsp").forward(req, resp);

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao buscar detalhe do frete.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void abrirConfirmarSaida(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            Frete frete = freteBO.buscarPorId(id);

            req.setAttribute("frete", frete);
            req.getRequestDispatcher("/WEB-INF/views/frete/confirmarSaida.jsp").forward(req, resp);

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao abrir confirmação de saída.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void abrirNaoEntregue(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            Frete frete = freteBO.buscarPorId(id);

            req.setAttribute("frete", frete);
            req.getRequestDispatcher("/WEB-INF/views/frete/naoEntregue.jsp").forward(req, resp);

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao abrir formulário de não entrega.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void emitir(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Frete frete = extrairFrete(req);
            freteBO.emitirFrete(frete);
            resp.sendRedirect(req.getContextPath() + "/fretes/listar?sucesso=emitido");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            try {
                req.setAttribute("clientes", clienteBO.listar(null, 1, Integer.MAX_VALUE));
                req.setAttribute("motoristas", motoristaBO.listar(null, 1, Integer.MAX_VALUE));
                req.setAttribute("veiculos", veiculoBO.listar(null, 1, Integer.MAX_VALUE));
            } catch (NegocioException ex) {
                LOGGER.log(Level.SEVERE, "Erro ao recarregar dados do formulário.", ex);
            }
            req.setAttribute("frete", extrairFreteSemValidacao(req));
            req.getRequestDispatcher("/WEB-INF/views/frete/form.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao emitir frete.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void confirmarSaida(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            String dataSaidaParam = req.getParameter("dataSaida");
            LocalDateTime dataSaida = LocalDateTime.parse(dataSaidaParam);
            freteBO.confirmarSaida(id, dataSaida);
            resp.sendRedirect(req.getContextPath() + "/fretes/detalhe?id=" + id + "&sucesso=saida");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            try {
                Long id = Long.parseLong(req.getParameter("id"));
                req.setAttribute("frete", freteBO.buscarPorId(id));
            } catch (Exception ex) {
            }
            req.getRequestDispatcher("/WEB-INF/views/frete/confirmarSaida.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao confirmar saída do frete.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void registrarEmTransito(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            freteBO.registrarEmTransito(id);
            resp.sendRedirect(req.getContextPath() + "/fretes/detalhe?id=" + id + "&sucesso=transito");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao registrar frete em trânsito.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void registrarEntrega(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            String dataEntregaParam = req.getParameter("dataEntrega");
            LocalDateTime dataEntrega = LocalDateTime.parse(dataEntregaParam);
            freteBO.registrarEntrega(id, dataEntrega);
            resp.sendRedirect(req.getContextPath() + "/fretes/detalhe?id=" + id + "&sucesso=entregue");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            try {
                Long id = Long.parseLong(req.getParameter("id"));
                req.setAttribute("frete", freteBO.buscarPorId(id));
            } catch (Exception ex) {
            }
            req.getRequestDispatcher("/WEB-INF/views/frete/detalhe.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao registrar entrega.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void registrarNaoEntregue(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            String dataParam = req.getParameter("dataOcorrencia");
            LocalDateTime dataOcorrencia = LocalDateTime.parse(dataParam);
            String motivo = req.getParameter("motivo");
            freteBO.registrarNaoEntregue(id, dataOcorrencia, motivo);
            resp.sendRedirect(req.getContextPath() + "/fretes/detalhe?id=" + id + "&sucesso=naoEntregue");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            try {
                Long id = Long.parseLong(req.getParameter("id"));
                req.setAttribute("frete", freteBO.buscarPorId(id));
            } catch (Exception ex) {
            }
            req.getRequestDispatcher("/WEB-INF/views/frete/naoEntregue.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao registrar não entrega.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private void cancelar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            freteBO.cancelar(id);
            resp.sendRedirect(req.getContextPath() + "/fretes/detalhe?id=" + id + "&sucesso=cancelado");

        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            listar(req, resp);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao cancelar frete.", e);
            resp.sendRedirect(req.getContextPath() + "/erro");
        }
    }

    private Frete extrairFrete(HttpServletRequest req) {
        Frete frete = new Frete();

        String idRemetente = req.getParameter("idRemetente");
        if (idRemetente != null && !idRemetente.isEmpty()) {
            Cliente remetente = new Cliente();
            try { remetente.setId(Long.parseLong(idRemetente)); } catch (NumberFormatException e) { /* mantém nulo */ }
            frete.setRemetente(remetente);
        }

        String idDestinatario = req.getParameter("idDestinatario");
        if (idDestinatario != null && !idDestinatario.isEmpty()) {
            Cliente destinatario = new Cliente();
            try { destinatario.setId(Long.parseLong(idDestinatario)); } catch (NumberFormatException e) { /* mantém nulo */ }
            frete.setDestinatario(destinatario);
        }

        String idMotorista = req.getParameter("idMotorista");
        if (idMotorista != null && !idMotorista.isEmpty()) {
            Motorista motorista = new Motorista();
            try { motorista.setId(Long.parseLong(idMotorista)); } catch (NumberFormatException e) { /* mantém nulo */ }
            frete.setMotorista(motorista);
        }

        String idVeiculo = req.getParameter("idVeiculo");
        if (idVeiculo != null && !idVeiculo.isEmpty()) {
            Veiculo veiculo = new Veiculo();
            try { veiculo.setId(Long.parseLong(idVeiculo)); } catch (NumberFormatException e) { /* mantém nulo */ }
            frete.setVeiculo(veiculo);
        }

        frete.setMunicipioOrigem(req.getParameter("municipioOrigem"));
        frete.setUfOrigem(req.getParameter("ufOrigem"));
        frete.setMunicipioDestino(req.getParameter("municipioDestino"));
        frete.setUfDestino(req.getParameter("ufDestino"));
        frete.setDescricaoCarga(req.getParameter("descricaoCarga"));

        String peso = req.getParameter("pesoCarga");
        if (peso != null && !peso.isEmpty()) {
            try { frete.setPesoCarga(new BigDecimal(peso)); } catch (NumberFormatException e) { /* mantém nulo */ }
        }

        String volumes = req.getParameter("volumeCarga");
        if (volumes != null && !volumes.isEmpty()) {
            try { frete.setVolumeCarga(Integer.parseInt(volumes)); } catch (NumberFormatException e) { /* mantém nulo */ }
        }

        String valorFrete = req.getParameter("valorFrete");
        if (valorFrete != null && !valorFrete.isEmpty()) {
            try { frete.setValorFrete(new BigDecimal(valorFrete)); } catch (NumberFormatException e) { /* mantém nulo */ }
        }

        String aliquota = req.getParameter("aliquotaIcms");
        if (aliquota != null && !aliquota.isEmpty()) {
            try { frete.setAliquotaIcms(new BigDecimal(aliquota)); } catch (NumberFormatException e) { /* mantém nulo */ }
        }

        String dataPrevisao = req.getParameter("dataPrevisaoEntrega");
        if (dataPrevisao != null && !dataPrevisao.isEmpty()) {
            try { frete.setDataPrevisaoEntrega(LocalDateTime.parse(dataPrevisao)); } catch (Exception e) { /* mantém nulo */ }
        }

        return frete;
    }

    private Frete extrairFreteSemValidacao(HttpServletRequest req) {
        return extrairFrete(req);
    }
}