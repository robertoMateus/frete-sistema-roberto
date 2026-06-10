package br.com.gwfrete.bo;

import br.com.gwfrete.dao.ManutencaoVeiculoDAO;
import br.com.gwfrete.dao.VeiculoDAO;
import br.com.gwfrete.exception.CadastroException;
import br.com.gwfrete.exception.VeiculoException;
import br.com.gwfrete.model.ManutencaoVeiculo;
import br.com.gwfrete.model.StatusVeiculo;
import br.com.gwfrete.model.Veiculo;
import br.com.gwfrete.util.ConexaoPool;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManutencaoVeiculoBO {

    private static final Logger LOGGER = Logger.getLogger(ManutencaoVeiculoBO.class.getName());
    private final ManutencaoVeiculoDAO manutencaoDAO = new ManutencaoVeiculoDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();

    public void registrar(ManutencaoVeiculo manutencao) throws CadastroException, VeiculoException {
        validarCamposObrigatorios(manutencao);

        try (Connection conn = ConexaoPool.getConexao()) {
            Veiculo veiculo = veiculoDAO.buscarPorId(manutencao.getVeiculo().getId(), conn);
            if (veiculo == null) {
                throw new VeiculoException("Veículo não encontrado.");
            }

            if (veiculo.getStatus() == StatusVeiculo.EM_VIAGEM) {
                throw new VeiculoException(
                        "Não é permitido registrar manutenção em um veículo que está em viagem.");
            }

            // Atualiza status do veículo para EM_MANUTENCAO dentro da mesma transação
            conn.setAutoCommit(false);
            try {
                manutencaoDAO.inserir(manutencao, conn);
                veiculoDAO.atualizarStatus(veiculo.getId(), StatusVeiculo.EM_MANUTENCAO, conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (CadastroException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao registrar manutenção.", e);
            throw new VeiculoException("Erro inesperado ao registrar manutenção.");
        }
    }

    public void concluir(Long id) throws CadastroException, VeiculoException {
        try (Connection conn = ConexaoPool.getConexao()) {
            ManutencaoVeiculo manutencao = manutencaoDAO.buscarPorId(id, conn);
            if (manutencao == null) {
                throw new VeiculoException("Manutenção não encontrada.");
            }

            if (manutencao.getDataFim() != null) {
                throw new VeiculoException("Esta manutenção já foi concluída.");
            }

            Veiculo veiculo = veiculoDAO.buscarPorId(manutencao.getVeiculo().getId(), conn);
            if (veiculo == null) {
                throw new VeiculoException("Veículo não encontrado.");
            }

            manutencao.setDataFim(java.time.LocalDate.now());

            // Atualiza manutenção e status do veículo dentro da mesma transação
            conn.setAutoCommit(false);
            try {
                manutencaoDAO.atualizar(manutencao, conn);

                // Só retorna para DISPONIVEL se não houver outra manutenção em aberto
                boolean outraManutencaoAberta = manutencaoDAO
                        .possuiManutencaoEmAberto(veiculo.getId(), manutencao.getId(), conn);
                if (!outraManutencaoAberta) {
                    veiculoDAO.atualizarStatus(veiculo.getId(), StatusVeiculo.DISPONIVEL, conn);
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (CadastroException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao concluir manutenção.", e);
            throw new VeiculoException("Erro inesperado ao concluir manutenção.");
        }
    }

    public void atualizar(ManutencaoVeiculo manutencao) throws CadastroException, VeiculoException {
        validarCamposObrigatorios(manutencao);

        try (Connection conn = ConexaoPool.getConexao()) {
            ManutencaoVeiculo existente = manutencaoDAO.buscarPorId(manutencao.getId(), conn);
            if (existente == null) {
                throw new VeiculoException("Manutenção não encontrada.");
            }

            if (existente.getDataFim() != null) {
                throw new VeiculoException(
                        "Não é permitido editar uma manutenção já concluída.");
            }

            manutencaoDAO.atualizar(manutencao, conn);

        } catch (CadastroException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar manutenção.", e);
            throw new VeiculoException("Erro inesperado ao atualizar manutenção.");
        }
    }

    public List<ManutencaoVeiculo> listarPorVeiculo(Long idVeiculo, int pagina, int itensPorPagina)
            throws VeiculoException {
        try (Connection conn = ConexaoPool.getConexao()) {
            return manutencaoDAO.listarPorVeiculo(idVeiculo, pagina, itensPorPagina, conn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar manutenções.", e);
            throw new VeiculoException("Erro inesperado ao listar manutenções.");
        }
    }

    public List<ManutencaoVeiculo> listarEmAberto(int pagina, int itensPorPagina) throws VeiculoException {
        try (Connection conn = ConexaoPool.getConexao()) {
            return manutencaoDAO.listarEmAberto(pagina, itensPorPagina, conn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar manutenções em aberto.", e);
            throw new VeiculoException("Erro inesperado ao listar manutenções em aberto.");
        }
    }

    public ManutencaoVeiculo buscarPorId(Long id) throws VeiculoException {
        try (Connection conn = ConexaoPool.getConexao()) {
            ManutencaoVeiculo manutencao = manutencaoDAO.buscarPorId(id, conn);
            if (manutencao == null) {
                throw new VeiculoException("Manutenção não encontrada.");
            }
            return manutencao;

        } catch (VeiculoException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar manutenção.", e);
            throw new VeiculoException("Erro inesperado ao buscar manutenção.");
        }
    }

    public int contarPorVeiculo(Long idVeiculo) throws VeiculoException {
        try (Connection conn = ConexaoPool.getConexao()) {
            return manutencaoDAO.contarPorVeiculo(idVeiculo, conn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao contar manutenções do veículo.", e);
            throw new VeiculoException("Erro inesperado ao contar manutenções.");
        }
    }

    public int contarEmAberto() throws VeiculoException {
        try (Connection conn = ConexaoPool.getConexao()) {
            return manutencaoDAO.contarEmAberto(conn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao contar manutenções em aberto.", e);
            throw new VeiculoException("Erro inesperado ao contar manutenções em aberto.");
        }
    }

    // validações privadas

    private void validarCamposObrigatorios(ManutencaoVeiculo manutencao) throws CadastroException {
        if (manutencao.getVeiculo() == null || manutencao.getVeiculo().getId() == null) {
            throw new CadastroException("O veículo é obrigatório.");
        }
        if (manutencao.getTipo() == null) {
            throw new CadastroException("O tipo de manutenção é obrigatório.");
        }
        if (manutencao.getDataInicio() == null) {
            throw new CadastroException("A data de início é obrigatória.");
        }
        if (manutencao.getCusto() != null && manutencao.getCusto().compareTo(BigDecimal.ZERO) < 0) {
            throw new CadastroException("O custo não pode ser negativo.");
        }
        if (manutencao.getDataFim() != null && manutencao.getDataFim().isBefore(manutencao.getDataInicio())) {
            throw new CadastroException("A data de fim não pode ser anterior à data de início.");
        }
    }
}