package br.com.gwfrete.bo;

import br.com.gwfrete.dao.ManutencaoVeiculoDAO;
import br.com.gwfrete.dao.VeiculoDAO;
import br.com.gwfrete.exception.CadastroException;
import br.com.gwfrete.exception.VeiculoException;
import br.com.gwfrete.model.StatusVeiculo;
import br.com.gwfrete.model.Veiculo;
import br.com.gwfrete.util.ConexaoPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VeiculoBO {

    private static final Logger LOGGER = Logger.getLogger(VeiculoBO.class.getName());
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();
    private final ManutencaoVeiculoDAO manutencaoDAO = new ManutencaoVeiculoDAO();

    public void cadastrar(Veiculo veiculo) throws CadastroException, VeiculoException {
        validarCamposObrigatorios(veiculo);
        validarFormatos(veiculo);

        try (Connection conn = ConexaoPool.getConexao()) {
            Veiculo existentePlaca = veiculoDAO.buscarPorPlaca(veiculo.getPlaca(), conn);
            if (existentePlaca != null) {
                throw new CadastroException("A placa informada já está cadastrada.");
            }

            if (veiculo.getRntrc() != null && !veiculo.getRntrc().trim().isEmpty()) {
                Veiculo existenteRntrc = veiculoDAO.buscarPorRntrc(veiculo.getRntrc(), conn);
                if (existenteRntrc != null) {
                    throw new CadastroException("O RNTRC informado já está cadastrado.");
                }
            }

            veiculo.setStatus(StatusVeiculo.DISPONIVEL);
            veiculoDAO.inserir(veiculo, conn);

        } catch (CadastroException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao cadastrar veículo.", e);
            throw new VeiculoException("Erro inesperado ao cadastrar veículo.");
        }
    }

    public void atualizar(Veiculo veiculo) throws CadastroException, VeiculoException {
        validarCamposObrigatorios(veiculo);
        validarFormatos(veiculo);

        try (Connection conn = ConexaoPool.getConexao()) {
            Veiculo existentePlaca = veiculoDAO.buscarPorPlaca(veiculo.getPlaca(), conn);
            if (existentePlaca != null && !existentePlaca.getId().equals(veiculo.getId())) {
                throw new CadastroException("A placa informada já está cadastrada para outro veículo.");
            }

            if (veiculo.getRntrc() != null && !veiculo.getRntrc().trim().isEmpty()) {
                Veiculo existenteRntrc = veiculoDAO.buscarPorRntrc(veiculo.getRntrc(), conn);
                if (existenteRntrc != null && !existenteRntrc.getId().equals(veiculo.getId())) {
                    throw new CadastroException("O RNTRC informado já está cadastrado para outro veículo.");
                }
            }

            veiculoDAO.atualizar(veiculo, conn);

        } catch (CadastroException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar veículo.", e);
            throw new VeiculoException("Erro inesperado ao atualizar veículo.");
        }
    }

    public void alterarStatusParaDisponivel(Long id) throws VeiculoException {
        try (Connection conn = ConexaoPool.getConexao()) {
            Veiculo veiculo = veiculoDAO.buscarPorId(id, conn);
            if (veiculo == null) {
                throw new VeiculoException("Veículo não encontrado.");
            }

            if (veiculo.getStatus() == StatusVeiculo.EM_VIAGEM) {
                throw new VeiculoException(
                        "Não é permitido alterar o status para Disponível manualmente " +
                        "enquanto o veículo estiver em viagem. O status é atualizado " +
                        "automaticamente ao concluir o frete.");
            }

            if (manutencaoDAO.possuiManutencaoEmAberto(id, conn)) {
                throw new VeiculoException(
                        "Não é permitido alterar o status para Disponível enquanto " +
                        "houver manutenção em aberto.");
            }

            veiculoDAO.atualizarStatus(id, StatusVeiculo.DISPONIVEL, conn);

        } catch (VeiculoException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao alterar status do veículo.", e);
            throw new VeiculoException("Erro inesperado ao alterar status do veículo.");
        }
    }

    public void alterarStatusParaManutencao(Long id) throws VeiculoException {
        try (Connection conn = ConexaoPool.getConexao()) {
            Veiculo veiculo = veiculoDAO.buscarPorId(id, conn);
            if (veiculo == null) {
                throw new VeiculoException("Veículo não encontrado.");
            }

            if (veiculo.getStatus() == StatusVeiculo.EM_VIAGEM) {
                throw new VeiculoException(
                        "Não é permitido colocar em manutenção um veículo que está em viagem.");
            }

            veiculoDAO.atualizarStatus(id, StatusVeiculo.EM_MANUTENCAO, conn);

        } catch (VeiculoException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao alterar status do veículo para manutenção.", e);
            throw new VeiculoException("Erro inesperado ao alterar status do veículo.");
        }
    }

    public void excluir(Long id) throws VeiculoException {
        try (Connection conn = ConexaoPool.getConexao()) {
            Veiculo veiculo = veiculoDAO.buscarPorId(id, conn);
            if (veiculo == null) {
                throw new VeiculoException("Veículo não encontrado.");
            }

            if (veiculo.getStatus() == StatusVeiculo.EM_VIAGEM) {
                throw new VeiculoException(
                        "Não é permitido excluir um veículo que está em viagem.");
            }

            if (veiculoDAO.possuiFreteEmTransito(id, conn)) {
                throw new VeiculoException(
                        "Não é permitido excluir um veículo com fretes vinculados.");
            }

            veiculoDAO.excluir(id, conn);

        } catch (VeiculoException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao excluir veículo.", e);
            throw new VeiculoException("Erro inesperado ao excluir veículo.");
        }
    }

    public Veiculo buscarPorId(Long id) throws VeiculoException {
        try (Connection conn = ConexaoPool.getConexao()) {
            Veiculo veiculo = veiculoDAO.buscarPorId(id, conn);
            if (veiculo == null) {
                throw new VeiculoException("Veículo não encontrado.");
            }
            return veiculo;

        } catch (VeiculoException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar veículo.", e);
            throw new VeiculoException("Erro inesperado ao buscar veículo.");
        }
    }

    public List<Veiculo> listar(String filtro, int pagina, int itensPorPagina) throws VeiculoException {
        try (Connection conn = ConexaoPool.getConexao()) {
            return veiculoDAO.listar(filtro, pagina, itensPorPagina, conn);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar veículos.", e);
            throw new VeiculoException("Erro inesperado ao listar veículos.");
        }
    }

    public int contarTotal(String filtro) throws VeiculoException {
        try (Connection conn = ConexaoPool.getConexao()) {
            return veiculoDAO.contarTotal(filtro, conn);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao contar veículos.", e);
            throw new VeiculoException("Erro inesperado ao contar veículos.");
        }
    }

    // — validações privadas —

    private void validarCamposObrigatorios(Veiculo veiculo) throws CadastroException {
        if (veiculo.getPlaca() == null || veiculo.getPlaca().trim().isEmpty()) {
            throw new CadastroException("A placa é obrigatória.");
        }
        if (veiculo.getRntrc() == null || veiculo.getRntrc().trim().isEmpty()) {
            throw new CadastroException("O RNTRC é obrigatório.");
        }
        if (veiculo.getAnoFabricacao() == null) {
            throw new CadastroException("O ano de fabricação é obrigatório.");
        }
        if (veiculo.getTipoVeiculo() == null) {
            throw new CadastroException("O tipo de veículo é obrigatório.");
        }
        if (veiculo.getTara() <= 0) {
            throw new CadastroException("A tara deve ser maior que zero.");
        }
        if (veiculo.getCapacidadeCarga() <= 0) {
            throw new CadastroException("A capacidade de carga deve ser maior que zero.");
        }
        if (veiculo.getVolume() <= 0) {
            throw new CadastroException("O volume deve ser maior que zero.");
        }
    }

    private void validarFormatos(Veiculo veiculo) throws CadastroException {
        String placa = veiculo.getPlaca().trim().toUpperCase();

        // Formato Mercosul: AAA0A00 — ou formato antigo: AAA0000
        boolean mercosul = placa.matches("[A-Z]{3}[0-9][A-Z][0-9]{2}");
        boolean antigo = placa.matches("[A-Z]{3}[0-9]{4}");

        if (!mercosul && !antigo) {
            throw new CadastroException(
                    "A placa informada é inválida. Use o formato Mercosul (ABC1D23) " +
                    "ou o formato antigo (ABC1234).");
        }

        veiculo.setPlaca(placa);

        if (veiculo.getAnoFabricacao() != null) {
            int anoAtual = LocalDate.now().getYear();
            if (veiculo.getAnoFabricacao() < 1950 || veiculo.getAnoFabricacao() > anoAtual + 1) {
                throw new CadastroException("O ano de fabricação informado é inválido.");
            }
        }
    }
}