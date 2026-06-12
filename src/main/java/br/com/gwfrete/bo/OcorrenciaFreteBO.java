package br.com.gwfrete.bo;

import br.com.gwfrete.dao.FreteDAO;
import br.com.gwfrete.dao.OcorrenciaFreteDAO;
import br.com.gwfrete.dao.VeiculoDAO;
import br.com.gwfrete.exception.CadastroException;
import br.com.gwfrete.exception.OcorrenciaException;
import br.com.gwfrete.model.Frete;
import br.com.gwfrete.model.OcorrenciaFrete;
import br.com.gwfrete.model.StatusFrete;
import br.com.gwfrete.model.StatusVeiculo;
import br.com.gwfrete.model.TipoOcorrencia;
import br.com.gwfrete.util.ConexaoPool;
import br.com.gwfrete.util.UfUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OcorrenciaFreteBO {

    private static final Logger LOGGER = Logger.getLogger(OcorrenciaFreteBO.class.getName());

    private final OcorrenciaFreteDAO ocorrenciaDAO = new OcorrenciaFreteDAO();
    private final FreteDAO freteDAO = new FreteDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();

    public void registrar(OcorrenciaFrete ocorrencia) throws CadastroException, OcorrenciaException {
        // Normaliza campos antes de qualquer validação
        normalizarCampos(ocorrencia);

        validarCamposObrigatorios(ocorrencia);

        try (Connection conn = ConexaoPool.getConexao()) {
            Frete frete = freteDAO.buscarPorId(ocorrencia.getFrete().getId(), conn);
            if (frete == null) {
                throw new OcorrenciaException("Frete não encontrado.");
            }

            validarFretePermiteOcorrencia(frete);

            validarDataOcorrencia(ocorrencia, frete);

            validarCronologia(ocorrencia, frete.getId(), conn);
            validarRegrasDoTipo(ocorrencia, frete, conn);

            // Tipos que exigem transação pois alteram outras tabelas
            if (ocorrencia.getTipo() == TipoOcorrencia.ENTREGA_REALIZADA) {
                registrarEntregaRealizada(ocorrencia, frete, conn);
            } else if (ocorrencia.getTipo() == TipoOcorrencia.SAIDA_PATIO) {
                registrarSaidaPatio(ocorrencia, frete, conn);
            } else if (ocorrencia.getTipo() == TipoOcorrencia.EM_ROTA
                    && frete.getStatus() == StatusFrete.SAIDA_CONFIRMADA) {
                registrarEmRota(ocorrencia, frete, conn);
            } else {
                ocorrenciaDAO.inserir(ocorrencia, conn);
            }

        } catch (CadastroException | OcorrenciaException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao registrar ocorrência.", e);
            throw new OcorrenciaException("Erro inesperado ao registrar ocorrência.");
        }
    }

    public List<OcorrenciaFrete> listarPorFrete(Long idFrete) throws OcorrenciaException {
        try (Connection conn = ConexaoPool.getConexao()) {
            return ocorrenciaDAO.listarPorFrete(idFrete, conn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar ocorrências do frete " + idFrete + ".", e);
            throw new OcorrenciaException("Erro inesperado ao listar ocorrências.");
        }
    }

    // Métodos de transação
    private void registrarSaidaPatio(OcorrenciaFrete ocorrencia, Frete frete, Connection conn)
            throws SQLException {
        conn.setAutoCommit(false);
        try {
            ocorrenciaDAO.inserir(ocorrencia, conn);
            freteDAO.atualizarStatus(frete.getId(), StatusFrete.SAIDA_CONFIRMADA, conn);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private void registrarEmRota(OcorrenciaFrete ocorrencia, Frete frete, Connection conn)
            throws SQLException {
        conn.setAutoCommit(false);
        try {
            ocorrenciaDAO.inserir(ocorrencia, conn);
            freteDAO.atualizarStatus(frete.getId(), StatusFrete.EM_TRANSITO, conn);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private void registrarEntregaRealizada(OcorrenciaFrete ocorrencia, Frete frete, Connection conn)
            throws SQLException {
        conn.setAutoCommit(false);
        try {
            ocorrenciaDAO.inserir(ocorrencia, conn);
            freteDAO.atualizarDataEntrega(frete.getId(), ocorrencia.getDataHoraOcorrencia(), conn);
            freteDAO.atualizarStatus(frete.getId(), StatusFrete.ENTREGUE, conn);
            veiculoDAO.atualizarStatus(frete.getVeiculo().getId(), StatusVeiculo.DISPONIVEL, conn);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private void normalizarCampos(OcorrenciaFrete ocorrencia) {
        if (ocorrencia.getMunicipio() != null) {
            ocorrencia.setMunicipio(ocorrencia.getMunicipio().trim().toUpperCase());
        }
        if (ocorrencia.getUf() != null) {
            ocorrencia.setUf(UfUtil.normalizar(ocorrencia.getUf()));
        }
        if (ocorrencia.getNomeRecebedor() != null) {
            ocorrencia.setNomeRecebedor(ocorrencia.getNomeRecebedor().trim());
        }
        if (ocorrencia.getDescricao() != null) {
            ocorrencia.setDescricao(ocorrencia.getDescricao().trim());
        }
    }

    private void validarCamposObrigatorios(OcorrenciaFrete ocorrencia) throws CadastroException {
        if (ocorrencia.getFrete() == null || ocorrencia.getFrete().getId() == null) {
            throw new CadastroException("O frete é obrigatório.");
        }
        if (ocorrencia.getTipo() == null) {
            throw new CadastroException("O tipo de ocorrência é obrigatório.");
        }
        if (ocorrencia.getDataHoraOcorrencia() == null) {
            throw new CadastroException("A data/hora da ocorrência é obrigatória.");
        }
        if (ocorrencia.getMunicipio() == null || ocorrencia.getMunicipio().trim().isEmpty()) {
            throw new CadastroException("O município da ocorrência é obrigatório.");
        }
        if (ocorrencia.getUf() == null || ocorrencia.getUf().trim().isEmpty()) {
            throw new CadastroException("A UF da ocorrência é obrigatória.");
        }
        if (!UfUtil.validarUf(ocorrencia.getUf())) {
            throw new CadastroException("A UF informada é inválida: " + ocorrencia.getUf());
        }
    }

    private void validarFretePermiteOcorrencia(Frete frete) throws OcorrenciaException {
        if (frete.getStatus() == StatusFrete.ENTREGUE
                || frete.getStatus() == StatusFrete.NAO_ENTREGUE
                || frete.getStatus() == StatusFrete.CANCELADO) {
            throw new OcorrenciaException(
                    "Não é permitido registrar ocorrência em frete com status '"
                            + frete.getStatus().getDescricao() + "'.");
        }
    }

    private void validarDataOcorrencia(OcorrenciaFrete ocorrencia, Frete frete)
            throws OcorrenciaException {
        if (frete.getDataEmissao() != null &&
                ocorrencia.getDataHoraOcorrencia().isBefore(frete.getDataEmissao())) {
            throw new OcorrenciaException(
                    "A data/hora da ocorrência não pode ser anterior à emissão do frete.");
        }
        if (frete.getDataSaida() != null &&
                ocorrencia.getDataHoraOcorrencia().isBefore(frete.getDataSaida())) {
            throw new OcorrenciaException(
                    "A data/hora da ocorrência não pode ser anterior à saída do frete.");
        }
        if(ocorrencia.getDataHoraOcorrencia().isAfter(LocalDateTime.now())){
            throw new OcorrenciaException("Não é possivel registrar ocorrência futura.");
        }
    }

    private void validarCronologia(OcorrenciaFrete ocorrencia, Long idFrete, Connection conn)
            throws OcorrenciaException, SQLException {
        OcorrenciaFrete ultima = ocorrenciaDAO.buscarUltimaPorFrete(idFrete, conn);
        if (ultima != null
                && !ocorrencia.getDataHoraOcorrencia().isAfter(ultima.getDataHoraOcorrencia())) {
            throw new OcorrenciaException(
                    "A data/hora da ocorrência deve ser posterior à última ocorrência registrada ("
                            + ultima.getDataHoraFormatada() + ").");
        }
    }

    private void validarRegrasDoTipo(OcorrenciaFrete ocorrencia, Frete frete, Connection conn)
            throws CadastroException, OcorrenciaException, SQLException {

        TipoOcorrencia tipo = ocorrencia.getTipo();

        // Descrição obrigatória para Avaria, Extravio e Outros
        if (tipo == TipoOcorrencia.AVARIA
                || tipo == TipoOcorrencia.EXTRAVIO
                || tipo == TipoOcorrencia.OUTROS) {
            if (ocorrencia.getDescricao() == null || ocorrencia.getDescricao().isEmpty()) {
                throw new CadastroException(
                        "A descrição é obrigatória para ocorrências do tipo " + tipo.name() + ".");
            }
        }

        if (tipo == TipoOcorrencia.SAIDA_PATIO
                && frete.getStatus() != StatusFrete.EMITIDO) {
            throw new OcorrenciaException(
                    "Ocorrência SAÍDA DO PÁTIO só pode ser registrada em frete com status EMITIDO.");
        }

        // EM_ROTA só após saída confirmada
        if (tipo == TipoOcorrencia.EM_ROTA) {
            if (frete.getStatus() != StatusFrete.SAIDA_CONFIRMADA
                    && frete.getStatus() != StatusFrete.EM_TRANSITO) {
                throw new OcorrenciaException(
                        "Ocorrência EM_ROTA só pode ser registrada após a saída confirmada.");
            }
        }

        // Nome e documento obrigatórios para Entrega Realizada
        if (tipo == TipoOcorrencia.ENTREGA_REALIZADA) {
            if (ocorrencia.getNomeRecebedor() == null
                    || ocorrencia.getNomeRecebedor().isEmpty()) {
                throw new CadastroException(
                        "O nome do recebedor é obrigatório para Entrega Realizada.");
            }
            if (ocorrencia.getDocumentoRecebedor() == null
                    || ocorrencia.getDocumentoRecebedor().isEmpty()) {
                throw new CadastroException(
                        "O documento do recebedor é obrigatório para Entrega Realizada.");
            }
            // Frete precisa estar EM_TRANSITO
            if (frete.getStatus() != StatusFrete.EM_TRANSITO) {
                throw new OcorrenciaException(
                        "Só é possível registrar Entrega Realizada em frete com status em trânsito.");
            }
            if (ocorrenciaDAO.jaPossuiEntregaRealizada(frete.getId(), conn)) {
                throw new OcorrenciaException(
                        "Este frete já possui uma entrega registrada.");
            }
        }
    }
}