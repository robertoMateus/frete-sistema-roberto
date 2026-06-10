package br.com.gwfrete.bo;

import br.com.gwfrete.dao.MotoristaDAO;
import br.com.gwfrete.exception.CadastroException;
import br.com.gwfrete.exception.MotoristaException;
import br.com.gwfrete.model.Motorista;
import br.com.gwfrete.model.StatusMotorista;
import br.com.gwfrete.util.ConexaoPool;
import br.com.gwfrete.util.CpfUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MotoristaBO {

    private static final Logger LOGGER = Logger.getLogger(MotoristaBO.class.getName());
    private final MotoristaDAO motoristaDAO = new MotoristaDAO();

    public void cadastrar(Motorista motorista) throws CadastroException, MotoristaException {
        validarCamposObrigatorios(motorista);
        validarFormatos(motorista);

        try (Connection conn = ConexaoPool.getConexao()) {
            Motorista existente = motoristaDAO.buscarPorCpf(motorista.getCpf(), conn);
            if (existente != null) {
                throw new CadastroException("O CPF informado já está cadastrado.");
            }

            motorista.setStatus(StatusMotorista.ATIVO);
            motoristaDAO.inserir(motorista, conn);

        } catch (CadastroException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao cadastrar motorista.", e);
            throw new MotoristaException("Erro inesperado ao cadastrar motorista.");
        }
    }

    public void atualizar(Motorista motorista) throws CadastroException, MotoristaException {
        validarCamposObrigatorios(motorista);
        validarFormatos(motorista);

        try (Connection conn = ConexaoPool.getConexao()) {
            Motorista existente = motoristaDAO.buscarPorCpf(motorista.getCpf(), conn);
            if (existente != null && !existente.getId().equals(motorista.getId())) {
                throw new CadastroException("O CPF informado já está cadastrado para outro motorista.");
            }

            motoristaDAO.atualizar(motorista, conn);

        } catch (CadastroException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar motorista.", e);
            throw new MotoristaException("Erro inesperado ao atualizar motorista.");
        }
    }

    public void inativar(Long id) throws MotoristaException {
        try (Connection conn = ConexaoPool.getConexao()) {
            Motorista motorista = motoristaDAO.buscarPorId(id, conn);
            if (motorista == null) {
                throw new MotoristaException("Motorista não encontrado.");
            }

            if (motoristaDAO.possuiFreteEmitido(id, conn)) {
                throw new MotoristaException(
                        "Não é permitido inativar um motorista com frete em status EMITIDO.");
            }
            if (motoristaDAO.possuiFreteAtivo(id, conn)) {
                throw new MotoristaException(
                        "Não é permitido inativar um motorista com frete em andamento.");
            }

            motorista.setStatus(StatusMotorista.INATIVO);
            motoristaDAO.atualizar(motorista, conn);

        } catch (MotoristaException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inativar motorista.", e);
            throw new MotoristaException("Erro inesperado ao inativar motorista.");
        }
    }

    public void ativar(Long id) throws MotoristaException {
        try (Connection conn = ConexaoPool.getConexao()) {
            Motorista motorista = motoristaDAO.buscarPorId(id, conn);
            if (motorista == null) {
                throw new MotoristaException("Motorista não encontrado.");
            }

            motorista.setStatus(StatusMotorista.ATIVO);
            motoristaDAO.atualizar(motorista, conn);

        } catch (MotoristaException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao ativar motorista.", e);
            throw new MotoristaException("Erro inesperado ao ativar motorista.");
        }
    }

    public void suspender(Long id) throws MotoristaException {
        try (Connection conn = ConexaoPool.getConexao()) {
            Motorista motorista = motoristaDAO.buscarPorId(id, conn);
            if (motorista == null) {
                throw new MotoristaException("Motorista não encontrado.");
            }

            if (motoristaDAO.possuiFreteAtivo(id, conn)) {
                throw new MotoristaException(
                        "Não é permitido suspender um motorista com frete em andamento.");
            }

            motorista.setStatus(StatusMotorista.SUSPENSO);
            motoristaDAO.atualizar(motorista, conn);

        } catch (MotoristaException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao suspender motorista.", e);
            throw new MotoristaException("Erro inesperado ao suspender motorista.");
        }
    }

    public void excluir(Long id) throws MotoristaException {
        try (Connection conn = ConexaoPool.getConexao()) {
            Motorista motorista = motoristaDAO.buscarPorId(id, conn);
            if (motorista == null) {
                throw new MotoristaException("Motorista não encontrado.");
            }

            if (motoristaDAO.possuiFreteAtivo(id, conn) || motoristaDAO.possuiFreteEmitido(id, conn)) {
                throw new MotoristaException(
                        "Não é permitido excluir um motorista com fretes cadastrados.");
            }

            motoristaDAO.excluir(id, conn);

        } catch (MotoristaException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao excluir motorista.", e);
            throw new MotoristaException("Erro inesperado ao excluir motorista.");
        }
    }

    public Motorista buscarPorId(Long id) throws MotoristaException {
        try (Connection conn = ConexaoPool.getConexao()) {
            Motorista motorista = motoristaDAO.buscarPorId(id, conn);
            if (motorista == null) {
                throw new MotoristaException("Motorista não encontrado.");
            }
            return motorista;

        } catch (MotoristaException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar motorista.", e);
            throw new MotoristaException("Erro inesperado ao buscar motorista.");
        }
    }

    public List<Motorista> listar(String filtro, int pagina, int itensPorPagina) throws MotoristaException {
        try (Connection conn = ConexaoPool.getConexao()) {
            return motoristaDAO.listar(filtro, pagina, itensPorPagina, conn);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar motoristas.", e);
            throw new MotoristaException("Erro inesperado ao listar motoristas.");
        }
    }

    public int contarTotal(String filtro) throws MotoristaException {
        try (Connection conn = ConexaoPool.getConexao()) {
            return motoristaDAO.contarTotal(filtro, conn);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao contar motoristas.", e);
            throw new MotoristaException("Erro inesperado ao contar motoristas.");
        }
    }

    public boolean isCnhVencida(Motorista motorista) {
        return motorista.getDataValidadeCnh().isBefore(LocalDate.now());
    }

    // validações privadas

    private void validarCamposObrigatorios(Motorista motorista) throws CadastroException {
        if (motorista.getNome() == null || motorista.getNome().trim().isEmpty()) {
            throw new CadastroException("O nome do motorista é obrigatório.");
        }
        if (motorista.getCpf() == null || motorista.getCpf().trim().isEmpty()) {
            throw new CadastroException("O CPF é obrigatório.");
        }
        if (motorista.getDataNascimento() == null) {
            throw new CadastroException("A data de nascimento é obrigatória.");
        }
        if (motorista.getTelefone() == null || motorista.getTelefone().trim().isEmpty()) {
            throw new CadastroException("O telefone é obrigatório");
        }
        if (motorista.getNumeroCnh() == null || motorista.getNumeroCnh().trim().isEmpty()) {
            throw new CadastroException("O número da CNH é obrigatório.");
        }
        if (motorista.getCategoriaCnh() == null) {
            throw new CadastroException("A categoria da CNH é obrigatória.");
        }
        if (motorista.getDataValidadeCnh() == null) {
            throw new CadastroException("A validade da CNH é obrigatória.");
        }
        if (motorista.getTipoVinculo() == null) {
            throw new CadastroException("O tipo de vínculo é obrigatório.");
        }
    }

    private void validarFormatos(Motorista motorista) throws CadastroException {
        String cpfLimpo = motorista.getCpf().replaceAll("[^0-9]", "");
        if (!CpfUtil.validarCpf(cpfLimpo)) {
            throw new CadastroException("O CPF informado é inválido.");
        }
        motorista.setCpf(cpfLimpo);

        String cnhLimpa = motorista.getNumeroCnh().replaceAll("[^0-9]", "");
        if (cnhLimpa.length() != 11) {
            throw new CadastroException("O número da CNH deve conter 11 dígitos.");
        }
        motorista.setNumeroCnh(cnhLimpa);
    }
}