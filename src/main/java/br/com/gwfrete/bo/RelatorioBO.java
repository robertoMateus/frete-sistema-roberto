package br.com.gwfrete.bo;

import br.com.gwfrete.dao.RelatorioDAO;
import br.com.gwfrete.exception.NegocioException;
import br.com.gwfrete.model.Motorista;
import br.com.gwfrete.util.ConexaoPool;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class RelatorioBO {

    private final RelatorioDAO relatorioDAO;

    public RelatorioBO() {
        this.relatorioDAO = new RelatorioDAO();
    }

    public Map<String, Object> montarParametrosFretesEmAberto() throws NegocioException {
        Connection conn = null;
        try {
            conn = ConexaoPool.getConexao();
            Map<String, Object> params = new HashMap<>();
            params.put("REPORT_TITLE", "Fretes em Aberto");
            params.put("DATA_GERACAO", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));
            return params;
        } catch (SQLException e) {
            throw new NegocioException("Erro ao acessar o banco de dados para o relatório.", e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
        }
    }

    public Map<String, Object> montarParametrosRomaneio(long idMotorista, Date dataRomaneio)
            throws NegocioException {

        if (idMotorista <= 0) {
            throw new NegocioException("Motorista inválido.");
        }
        if (dataRomaneio == null) {
            throw new NegocioException("Data do romaneio é obrigatória.");
        }

        Connection conn = null;
        try {
            conn = ConexaoPool.getConexao();

            Motorista motorista = relatorioDAO.buscarMotoristaComVeiculo(conn, idMotorista);
            if (motorista == null) {
                throw new NegocioException("Motorista não encontrado.");
            }

            int totalFretes = relatorioDAO.contarFretesDoMotoristaData(conn, idMotorista, dataRomaneio);
            if (totalFretes == 0) {
                String dataFormatada = new SimpleDateFormat("dd/MM/yyyy").format(dataRomaneio);
                throw new NegocioException(
                        "Nenhum frete encontrado para " + motorista.getNome() + " em " + dataFormatada + ".");
            }

            Map<String, Object> params = new HashMap<>();
            params.put("ID_MOTORISTA", idMotorista);
            params.put("DATA_ROMANEIO", dataRomaneio);
            params.put("NOME_MOTORISTA", motorista.getNome());
            params.put("CPF_MOTORISTA", motorista.getCpfFormatado());
            params.put("PLACA_VEICULO",
                    motorista.getVeiculo() != null ? motorista.getVeiculo().getPlaca() : null);
            params.put("TIPO_VEICULO",
                    motorista.getVeiculo() != null && motorista.getVeiculo().getTipoVeiculo() != null
                            ? motorista.getVeiculo().getTipoVeiculo().name()
                            : null);

            return params;

        } catch (NegocioException e) {
            throw e;
        } catch (SQLException e) {
            throw new NegocioException("Erro ao acessar o banco de dados para o romaneio.", e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
        }
    }
}