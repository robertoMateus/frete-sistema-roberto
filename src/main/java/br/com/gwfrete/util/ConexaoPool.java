package br.com.gwfrete.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.dbcp2.BasicDataSource;

public class ConexaoPool {

    private static final Logger LOGGER = Logger.getLogger(ConexaoPool.class.getName());
    private static BasicDataSource dataSource;

    private ConexaoPool() {
    }


    public static void inicializar() {
        Properties props = new Properties();

        if (dataSource != null) {
            LOGGER.warning("Pool de conexões já foi inicializado.");
            return;
        }
 
        try (InputStream input = ConexaoPool.class
                .getClassLoader()
                .getResourceAsStream("db/db.properties")) {
 
            if (input == null) {
                throw new RuntimeException("Arquivo db.properties não encontrado.");
            }
 
            props.load(input);
 
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar db.properties", e);
            throw new RuntimeException("Erro ao carregar configurações do banco de dados.", e);
        }
 
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName(props.getProperty("db.driver"));
        dataSource.setUrl(props.getProperty("db.url"));
        dataSource.setUsername(props.getProperty("db.username"));
        dataSource.setPassword(props.getProperty("db.password"));
 
        // Configurações do pool
        dataSource.setInitialSize(Integer.parseInt(props.getProperty("db.pool.initialSize", "5")));
        dataSource.setMaxTotal(Integer.parseInt(props.getProperty("db.pool.maxTotal", "20")));
        dataSource.setMaxIdle(Integer.parseInt(props.getProperty("db.pool.maxIdle", "10")));
        dataSource.setMinIdle(Integer.parseInt(props.getProperty("db.pool.minIdle", "5")));
        dataSource.setMaxWaitMillis(Long.parseLong(props.getProperty("db.pool.maxWaitMillis", "10000")));
 
        // Valida conexão ao pegar do pool
        dataSource.setTestOnBorrow(true);
        dataSource.setValidationQuery("SELECT 1");
 
        LOGGER.info("Pool de conexões inicializado com sucesso.");
    }

    public static Connection getConexao() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Pool de conexões não foi inicializado.");
        }
        return dataSource.getConnection();
    }

    public static void encerrar() {
        if (dataSource != null) {
            try {
                dataSource.close();
                LOGGER.info("Pool de conexões encerrado com sucesso.");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erro ao encerrar pool de conexões", e);
            }
        }
    }


}
