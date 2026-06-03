package br.com.gwfrete.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import br.com.gwfrete.util.ConexaoPool;

@WebListener
public class AppContextListener implements ServletContextListener{
    
    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(javax.servlet.ServletContextEvent sce) {
        LOGGER.info("Iniciando aplicação.");
        try {
            ConexaoPool.inicializar();
            LOGGER.info("Aplicação iniciada com sucesso.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro crítico ao inicializar a aplicação.", e);
            throw new RuntimeException("Falha ao inicializar o pool de conexões.", e);
        }

    }

    @Override
    public void contextDestroyed(javax.servlet.ServletContextEvent sce) {
        LOGGER.info("Encerrando aplicação.");
        try {
            ConexaoPool.encerrar();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao encerrar a aplicação.", e);
        }
        LOGGER.info("Aplicação encerrada com sucesso.");
    }
}
