package br.com.gwfrete.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthFilter implements Filter {
    // Libera sem autenticação
    private static final String[] ROTAS_LIBERADAS = {
            "/auth/login",
            "/auth/logout"
    };
    // Libera recursos
    private static final String[] RECURSOS_LIBERADOS = {
            "/css/",
            "/js/",
            "/images/",
            "/favicon.ico"
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        // Remove o context path da URI para comparação
        String path = uri.substring(contextPath.length());

        // Libera recursos estáticos
        for (String recurso : RECURSOS_LIBERADOS) {
            if (path.startsWith(recurso)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // Libera rotas de autenticação
        for (String rota : ROTAS_LIBERADAS) {
            if (path.startsWith(rota)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // Verifica se o usuário está logado
        HttpSession session = httpRequest.getSession(false);
        boolean logado = session != null && session.getAttribute("usuarioLogado") != null;

        if (logado) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(contextPath + "/auth/login");
        }

    }

}
