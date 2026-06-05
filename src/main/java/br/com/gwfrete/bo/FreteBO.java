package br.com.gwfrete.bo;

import br.com.gwfrete.dao.ClienteDAO;
import br.com.gwfrete.dao.FreteDAO;
import br.com.gwfrete.dao.MotoristaDAO;
import br.com.gwfrete.dao.VeiculoDAO;
import br.com.gwfrete.exception.CadastroException;
import br.com.gwfrete.exception.FreteException;
import br.com.gwfrete.model.Cliente;
import br.com.gwfrete.model.Frete;
import br.com.gwfrete.model.Motorista;
import br.com.gwfrete.model.StatusFrete;
import br.com.gwfrete.model.StatusMotorista;
import br.com.gwfrete.model.StatusVeiculo;
import br.com.gwfrete.model.Veiculo;
import br.com.gwfrete.util.ConexaoPool;
import br.com.gwfrete.util.FreteUtil;
import br.com.gwfrete.util.UfUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FreteBO {

    private static final Logger LOGGER = Logger.getLogger(FreteBO.class.getName());

    private final FreteDAO freteDAO = new FreteDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final MotoristaDAO motoristaDAO = new MotoristaDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();
    private final PrecoRotaBO precoRotaBO = new PrecoRotaBO();

    public void emitirFrete(Frete frete) throws CadastroException, FreteException {
        validarCamposObrigatorios(frete);

        try (Connection conn = ConexaoPool.getConexao()) {
            // carrega entidades completas para validação
            Cliente remetente = clienteDAO.buscarPorId(frete.getRemetente().getId(), conn);
            Cliente destinatario = clienteDAO.buscarPorId(frete.getDestinatario().getId(), conn);
            Motorista motorista = motoristaDAO.buscarPorId(frete.getMotorista().getId(), conn);
            Veiculo veiculo = veiculoDAO.buscarPorId(frete.getVeiculo().getId(), conn);

            validarEntidadesExistem(remetente, destinatario, motorista, veiculo);

            frete.setDataEmissao(LocalDateTime.now());

            validarMotorista(motorista, frete.getDataEmissao().toLocalDate(), conn);
            validarVeiculo(veiculo, frete.getPesoCarga());
            validarDatas(frete);
            validarUfs(frete);

            // gera número no BO via sequence
            long sequencial = freteDAO.buscarProximoSequencial(conn);
            String numero = FreteUtil.formatarNumeroFrete(sequencial);
            frete.setNumeroFrete(numero);
            frete.setStatus(StatusFrete.EMITIDO);

            // calcula ICMS se alíquota informada
            calcularValoresFinanceiros(frete);

            freteDAO.inserir(frete, conn);

        } catch (CadastroException | FreteException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao emitir frete.", e);
            throw new FreteException("Erro inesperado ao emitir frete.");
        }
    }

    public void confirmarSaida(Long idFrete, LocalDateTime dataSaida) throws FreteException {
        if (dataSaida == null) {
            throw new FreteException("A data/hora de saída é obrigatória.");
        }

        try (Connection conn = ConexaoPool.getConexao()) {
            Frete frete = freteDAO.buscarPorId(idFrete, conn);
            validarFreteEncontrado(frete, idFrete);

            if (frete.getStatus() != StatusFrete.EMITIDO) {
                throw new FreteException(
                        "Não é possível confirmar saída de um frete com status '"
                                + frete.getStatus().getDescricao() + "'. "
                                + "O frete deve estar com status EMITIDO.");
            }

            if (dataSaida.isBefore(frete.getDataEmissao())) {
                throw new FreteException(
                        "A data de saída não pode ser anterior à data de emissão do frete.");
            }

            conn.setAutoCommit(false);
            try {
                freteDAO.atualizarDataSaida(idFrete, dataSaida, conn);
                freteDAO.atualizarStatus(idFrete, StatusFrete.SAIDA_CONFIRMADA, conn);
                veiculoDAO.atualizarStatus(frete.getVeiculo().getId(), StatusVeiculo.EM_VIAGEM, conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (FreteException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao confirmar saída do frete " + idFrete + ".", e);
            throw new FreteException("Erro inesperado ao confirmar saída do frete.");
        }
    }

    public void registrarEmTransito(Long idFrete) throws FreteException {
        try (Connection conn = ConexaoPool.getConexao()) {
            Frete frete = freteDAO.buscarPorId(idFrete, conn);
            validarFreteEncontrado(frete, idFrete);

            if (frete.getStatus() != StatusFrete.SAIDA_CONFIRMADA) {
                throw new FreteException(
                        "Não é possível registrar em trânsito um frete com status '"
                                + frete.getStatus().getDescricao() + "'. "
                                + "O frete deve estar com status SAÍDA CONFIRMADA.");
            }

            freteDAO.atualizarStatus(idFrete, StatusFrete.EM_TRANSITO, conn);

        } catch (FreteException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao registrar frete em trânsito " + idFrete + ".", e);
            throw new FreteException("Erro inesperado ao registrar frete em trânsito.");
        }
    }

    public void registrarEntrega(Long idFrete, LocalDateTime dataEntrega) throws FreteException {
        if (dataEntrega == null) {
            throw new FreteException("A data/hora de entrega é obrigatória.");
        }

        try (Connection conn = ConexaoPool.getConexao()) {
            Frete frete = freteDAO.buscarPorId(idFrete, conn);
            validarFreteEncontrado(frete, idFrete);

            if (frete.getStatus() != StatusFrete.EM_TRANSITO) {
                throw new FreteException(
                        "Não é possível registrar entrega de um frete com status '"
                                + frete.getStatus().getDescricao() + "'. "
                                + "O frete deve estar com status EM TRÂNSITO.");
            }

            if (dataEntrega.isBefore(frete.getDataSaida())) {
                throw new FreteException(
                        "A data de entrega não pode ser anterior à data de saída do frete.");
            }

            conn.setAutoCommit(false);
            try {
                freteDAO.atualizarDataEntrega(idFrete, dataEntrega, conn);
                freteDAO.atualizarStatus(idFrete, StatusFrete.ENTREGUE, conn);
                veiculoDAO.atualizarStatus(frete.getVeiculo().getId(), StatusVeiculo.DISPONIVEL, conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (FreteException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao registrar entrega do frete " + idFrete + ".", e);
            throw new FreteException("Erro inesperado ao registrar entrega do frete.");
        }
    }

    public void registrarNaoEntregue(Long idFrete, LocalDateTime dataOcorrencia, String motivo)
            throws FreteException {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new FreteException("O motivo da não entrega é obrigatório.");
        }
        if (dataOcorrencia == null) {
            throw new FreteException("A data/hora da ocorrência é obrigatória.");
        }

        try (Connection conn = ConexaoPool.getConexao()) {
            Frete frete = freteDAO.buscarPorId(idFrete, conn);
            validarFreteEncontrado(frete, idFrete);

            if (frete.getStatus() != StatusFrete.EM_TRANSITO) {
                throw new FreteException(
                        "Não é possível registrar não entrega de um frete com status '"
                                + frete.getStatus().getDescricao() + "'. "
                                + "O frete deve estar com status EM TRÂNSITO.");
            }

            conn.setAutoCommit(false);
            try {
                freteDAO.atualizarStatus(idFrete, StatusFrete.NAO_ENTREGUE, conn);
                veiculoDAO.atualizarStatus(frete.getVeiculo().getId(), StatusVeiculo.DISPONIVEL, conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (FreteException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao registrar não entrega do frete " + idFrete + ".", e);
            throw new FreteException("Erro inesperado ao registrar não entrega do frete.");
        }
    }

    public void cancelar(Long idFrete) throws FreteException {
        try (Connection conn = ConexaoPool.getConexao()) {
            Frete frete = freteDAO.buscarPorId(idFrete, conn);
            validarFreteEncontrado(frete, idFrete);

            if (frete.getStatus() == StatusFrete.CANCELADO) {
                throw new FreteException("O frete já está cancelado.");
            }

            if (frete.getStatus() == StatusFrete.SAIDA_CONFIRMADA
                    || frete.getStatus() == StatusFrete.EM_TRANSITO
                    || frete.getStatus() == StatusFrete.ENTREGUE
                    || frete.getStatus() == StatusFrete.NAO_ENTREGUE) {
                throw new FreteException(
                        "Não é possível cancelar um frete com status '"
                                + frete.getStatus().getDescricao() + "'. "
                                + "O cancelamento só é permitido antes da saída ser confirmada.");
            }

            freteDAO.atualizarStatus(idFrete, StatusFrete.CANCELADO, conn);

        } catch (FreteException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao cancelar frete " + idFrete + ".", e);
            throw new FreteException("Erro inesperado ao cancelar frete.");
        }
    }

    public Frete buscarPorId(Long id) throws FreteException {
        try (Connection conn = ConexaoPool.getConexao()) {
            Frete frete = freteDAO.buscarPorId(id, conn);
            if (frete == null) {
                throw new FreteException("Frete não encontrado.");
            }
            return frete;

        } catch (FreteException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar frete " + id + ".", e);
            throw new FreteException("Erro inesperado ao buscar frete.");
        }
    }

    public List<Frete> listar(String filtro, int pagina, int itensPorPagina) throws FreteException {
        try (Connection conn = ConexaoPool.getConexao()) {
            return freteDAO.listar(filtro, pagina, itensPorPagina, conn);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar fretes.", e);
            throw new FreteException("Erro inesperado ao listar fretes.");
        }
    }

    public int contarTotal(String filtro) throws FreteException {
        try (Connection conn = ConexaoPool.getConexao()) {
            return freteDAO.contarTotal(filtro, conn);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao contar fretes.", e);
            throw new FreteException("Erro inesperado ao contar fretes.");
        }
    }

    public List<Frete> listarEmAberto() throws FreteException {
        try (Connection conn = ConexaoPool.getConexao()) {
            return freteDAO.listarEmAberto(conn);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar fretes em aberto.", e);
            throw new FreteException("Erro inesperado ao listar fretes em aberto.");
        }
    }

    public List<Frete> listarPorMotoristaEData(Long idMotorista, LocalDate data) throws FreteException {
        try (Connection conn = ConexaoPool.getConexao()) {
            return freteDAO.listarPorMotoristaEData(idMotorista, data, conn);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar fretes por motorista e data.", e);
            throw new FreteException("Erro inesperado ao listar fretes por motorista e data.");
        }
    }

    public BigDecimal sugerirValorFrete(String municipioOrigem, String ufOrigem,
            String municipioDestino, String ufDestino,
            BigDecimal pesoKg) throws FreteException {
        try {
            return precoRotaBO.calcularValorSugerido(
                    municipioOrigem, ufOrigem, municipioDestino, ufDestino, pesoKg);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Não foi possível calcular valor sugerido para a rota.", e);
            return null;
        }
    }

    // Validações privadas
    private void validarCamposObrigatorios(Frete frete) throws CadastroException {
        if (frete.getRemetente() == null || frete.getRemetente().getId() == null) {
            throw new CadastroException("O remetente é obrigatório.");
        }
        if (frete.getDestinatario() == null || frete.getDestinatario().getId() == null) {
            throw new CadastroException("O destinatário é obrigatório.");
        }
        if (frete.getMotorista() == null || frete.getMotorista().getId() == null) {
            throw new CadastroException("O motorista é obrigatório.");
        }
        if (frete.getVeiculo() == null || frete.getVeiculo().getId() == null) {
            throw new CadastroException("O veículo é obrigatório.");
        }
        if (frete.getMunicipioOrigem() == null || frete.getMunicipioOrigem().trim().isEmpty()) {
            throw new CadastroException("O município de origem é obrigatório.");
        }
        if (frete.getUfOrigem() == null || frete.getUfOrigem().trim().isEmpty()) {
            throw new CadastroException("A UF de origem é obrigatória.");
        }
        if (frete.getMunicipioDestino() == null || frete.getMunicipioDestino().trim().isEmpty()) {
            throw new CadastroException("O município de destino é obrigatório.");
        }
        if (frete.getUfDestino() == null || frete.getUfDestino().trim().isEmpty()) {
            throw new CadastroException("A UF de destino é obrigatória.");
        }
        if (frete.getDescricaoCarga() == null || frete.getDescricaoCarga().trim().isEmpty()) {
            throw new CadastroException("A descrição da carga é obrigatória.");
        }
        if (frete.getPesoCarga() == null || frete.getPesoCarga().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CadastroException("O peso da carga deve ser maior que zero.");
        }
        if (frete.getVolumeCarga() == null || frete.getVolumeCarga() <= 0) {
            throw new CadastroException("O número de volumes deve ser maior que zero.");
        }
        if (frete.getValorFrete() == null || frete.getValorFrete().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CadastroException("O valor do frete deve ser maior que zero.");
        }
        if (frete.getDataPrevisaoEntrega() == null) {
            throw new CadastroException("A data prevista de entrega é obrigatória.");
        }
    }

    private void validarEntidadesExistem(Cliente remetente, Cliente destinatario,
            Motorista motorista, Veiculo veiculo)
            throws FreteException {
        if (remetente == null) {
            throw new FreteException("Remetente não encontrado.");
        }
        if (destinatario == null) {
            throw new FreteException("Destinatário não encontrado.");
        }
        if (motorista == null) {
            throw new FreteException("Motorista não encontrado.");
        }
        if (veiculo == null) {
            throw new FreteException("Veículo não encontrado.");
        }
    }

    private void validarMotorista(Motorista motorista, LocalDate dataEmissao, Connection conn)
            throws FreteException, SQLException {
        if (motorista.getStatus() != StatusMotorista.ATIVO) {
            throw new FreteException(
                    "O motorista '" + motorista.getNome() + "' não está ativo.");
        }

        // CNH deve estar válida na data de emissão do frete
        if (motorista.getDataValidadeCnh() == null
                || motorista.getDataValidadeCnh().isBefore(dataEmissao)) {
            throw new FreteException(
                    "A CNH do motorista '" + motorista.getNome()
                            + "' está vencida ou com validade inválida.");
        }

        // Motorista não pode ter frete em SAIDA_CONFIRMADA ou EM_TRANSITO
        boolean emViagem = freteDAO.motoristaTemFreteAtivo(motorista.getId(), conn);
        if (emViagem) {
            throw new FreteException(
                    "O motorista '" + motorista.getNome()
                            + "' já possui um frete em andamento (saída confirmada ou em trânsito).");
        }
    }

    private void validarVeiculo(Veiculo veiculo, BigDecimal pesoCarga) throws FreteException {
        if (veiculo.getStatus() != StatusVeiculo.DISPONIVEL) {
            throw new FreteException(
                    "O veículo de placa '" + veiculo.getPlaca()
                            + "' não está disponível. Status atual: "
                            + veiculo.getStatus().getDescricao() + ".");
        }

        if (veiculo.getCapacidadeCarga() > 0
                && pesoCarga.compareTo(BigDecimal.valueOf(veiculo.getCapacidadeCarga())) > 0) {
            throw new FreteException(
                    "O peso da carga (" + pesoCarga + " kg) excede a capacidade do veículo ("
                            + veiculo.getCapacidadeCarga() + " kg).");
        }
    }

    private void validarDatas(Frete frete) throws FreteException {
        LocalDateTime dataEmissao = frete.getDataEmissao() != null
                ? frete.getDataEmissao()
                : LocalDateTime.now();

        if (!frete.getDataPrevisaoEntrega().isAfter(dataEmissao)) {
            throw new FreteException(
                    "A data prevista de entrega deve ser posterior à data de emissão do frete.");
        }
    }

    private void validarUfs(Frete frete) throws CadastroException {
        if (!UfUtil.validarUf(frete.getUfOrigem())) {
            throw new CadastroException(
                    "A UF de origem '" + frete.getUfOrigem() + "' é inválida.");
        }
        if (!UfUtil.validarUf(frete.getUfDestino())) {
            throw new CadastroException(
                    "A UF de destino '" + frete.getUfDestino() + "' é inválida.");
        }
    }

    private void calcularValoresFinanceiros(Frete frete) {
        BigDecimal valorFrete = frete.getValorFrete();

        if (frete.getAliquotaIcms() != null
                && frete.getAliquotaIcms().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal aliquota = frete.getAliquotaIcms()
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal valorIcms = valorFrete.multiply(aliquota)
                    .setScale(2, RoundingMode.HALF_UP);
            frete.setValorIcms(valorIcms);
            frete.setValorTotal(valorFrete.add(valorIcms));
        } else {
            frete.setValorIcms(BigDecimal.ZERO);
            frete.setValorTotal(valorFrete);
        }
    }

    private void validarFreteEncontrado(Frete frete, Long id) throws FreteException {
        if (frete == null) {
            throw new FreteException("Frete #" + id + " não encontrado.");
        }
    }
}