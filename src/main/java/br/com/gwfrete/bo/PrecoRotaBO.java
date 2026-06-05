package br.com.gwfrete.bo;

import br.com.gwfrete.dao.PrecoRotaDAO;
import br.com.gwfrete.exception.CadastroException;
import br.com.gwfrete.exception.NegocioException;
import br.com.gwfrete.model.PrecoRota;
import br.com.gwfrete.util.ConexaoPool;
import br.com.gwfrete.util.UfUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrecoRotaBO {

    private static final Logger LOGGER = Logger.getLogger(PrecoRotaBO.class.getName());
    private final PrecoRotaDAO precoRotaDAO = new PrecoRotaDAO();

    public void cadastrar(PrecoRota precoRota) throws CadastroException, NegocioException {
        normalizarCampos(precoRota);
        validarCamposObrigatorios(precoRota);
        validarRota(precoRota);

        try (Connection conn = ConexaoPool.getConexao()) {
            PrecoRota existente = precoRotaDAO.buscarPorRota(
                    precoRota.getMunicipioOrigem(),
                    precoRota.getUfOrigem(),
                    precoRota.getMunicipioDestino(),
                    precoRota.getUfDestino(),
                    conn);

            if (existente != null) {
                throw new CadastroException(
                        "Já existe um preço cadastrado para a rota "
                        + precoRota.getMunicipioOrigem() + "/" + precoRota.getUfOrigem()
                        + " → "
                        + precoRota.getMunicipioDestino() + "/" + precoRota.getUfDestino()
                        + ".");
            }

            precoRotaDAO.inserir(precoRota, conn);

        } catch (CadastroException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao cadastrar preço de rota.", e);
            throw new NegocioException("Erro inesperado ao cadastrar preço de rota.");
        }
    }

    public void atualizar(PrecoRota precoRota) throws CadastroException, NegocioException {
        normalizarCampos(precoRota);
        validarCamposObrigatorios(precoRota);
        validarRota(precoRota);

        try (Connection conn = ConexaoPool.getConexao()) {
            PrecoRota registroAtual = precoRotaDAO.buscarPorId(precoRota.getId(), conn);
            if (registroAtual == null) {
                throw new NegocioException("Preço de rota não encontrado.");
            }

            PrecoRota existente = precoRotaDAO.buscarPorRota(
                    precoRota.getMunicipioOrigem(),
                    precoRota.getUfOrigem(),
                    precoRota.getMunicipioDestino(),
                    precoRota.getUfDestino(),
                    conn);

            if (existente != null && !existente.getId().equals(precoRota.getId())) {
                throw new CadastroException("Já existe um preço cadastrado para esta rota.");
            }

            precoRotaDAO.atualizar(precoRota, conn);

        } catch (CadastroException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar preço de rota.", e);
            throw new NegocioException("Erro inesperado ao atualizar preço de rota.");
        }
    }

    public void excluir(Long id) throws NegocioException {
        try (Connection conn = ConexaoPool.getConexao()) {
            PrecoRota precoRota = precoRotaDAO.buscarPorId(id, conn);
            if (precoRota == null) {
                throw new NegocioException("Preço de rota não encontrado.");
            }

            precoRotaDAO.excluir(id, conn);

        } catch (NegocioException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao excluir preço de rota.", e);
            throw new NegocioException("Erro inesperado ao excluir preço de rota.");
        }
    }

    public PrecoRota buscarPorId(Long id) throws NegocioException {
        try (Connection conn = ConexaoPool.getConexao()) {
            PrecoRota precoRota = precoRotaDAO.buscarPorId(id, conn);
            if (precoRota == null) {
                throw new NegocioException("Preço de rota não encontrado.");
            }
            return precoRota;

        } catch (NegocioException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar preço de rota.", e);
            throw new NegocioException("Erro inesperado ao buscar preço de rota.");
        }
    }

    public PrecoRota buscarPorRota(String municipioOrigem, String ufOrigem,
                                   String municipioDestino, String ufDestino)
            throws NegocioException {
        try (Connection conn = ConexaoPool.getConexao()) {
            return precoRotaDAO.buscarPorRota(
                    municipioOrigem, ufOrigem,
                    municipioDestino, ufDestino,
                    conn);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar preço de rota.", e);
            throw new NegocioException("Erro inesperado ao buscar preço de rota.");
        }
    }

    public BigDecimal calcularValorSugerido(String municipioOrigem, String ufOrigem,
                                            String municipioDestino, String ufDestino,
                                            BigDecimal pesoKg)
            throws CadastroException, NegocioException {

        if (pesoKg != null && pesoKg.compareTo(BigDecimal.ZERO) < 0) {
            throw new CadastroException("O peso não pode ser negativo.");
        }

        PrecoRota rota = buscarPorRota(municipioOrigem, ufOrigem, municipioDestino, ufDestino);
        if (rota == null) {
            return null; // rota sem preço cadastrado — valor fica a critério do usuário
        }

        BigDecimal valor = rota.getValorBase();
        if (rota.getValorPorKg() != null
                && pesoKg != null
                && pesoKg.compareTo(BigDecimal.ZERO) > 0) {
            valor = valor.add(rota.getValorPorKg().multiply(pesoKg));
        }

        return valor;
    }

    public List<PrecoRota> listar(String filtro, int pagina, int itensPorPagina)
            throws NegocioException {
        try (Connection conn = ConexaoPool.getConexao()) {
            return precoRotaDAO.listar(filtro, pagina, itensPorPagina, conn);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar preços de rota.", e);
            throw new NegocioException("Erro inesperado ao listar preços de rota.");
        }
    }

    public int contarTotal(String filtro) throws NegocioException {
        try (Connection conn = ConexaoPool.getConexao()) {
            return precoRotaDAO.contarTotal(filtro, conn);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao contar preços de rota.", e);
            throw new NegocioException("Erro inesperado ao contar preços de rota.");
        }
    }

    // — métodos privados —

    private void normalizarCampos(PrecoRota precoRota) {
        if (precoRota.getMunicipioOrigem() != null) {
            precoRota.setMunicipioOrigem(precoRota.getMunicipioOrigem().trim().toUpperCase());
        }
        if (precoRota.getMunicipioDestino() != null) {
            precoRota.setMunicipioDestino(precoRota.getMunicipioDestino().trim().toUpperCase());
        }
        if (precoRota.getUfOrigem() != null) {
            precoRota.setUfOrigem(UfUtil.normalizar(precoRota.getUfOrigem()));
        }
        if (precoRota.getUfDestino() != null) {
            precoRota.setUfDestino(UfUtil.normalizar(precoRota.getUfDestino()));
        }
    }

    private void validarCamposObrigatorios(PrecoRota precoRota) throws CadastroException {
        if (precoRota.getMunicipioOrigem() == null || precoRota.getMunicipioOrigem().isEmpty()) {
            throw new CadastroException("O município de origem é obrigatório.");
        }
        if (precoRota.getUfOrigem() == null || precoRota.getUfOrigem().isEmpty()) {
            throw new CadastroException("A UF de origem é obrigatória.");
        }
        if (!UfUtil.validarUf(precoRota.getUfOrigem())) {
            throw new CadastroException("A UF de origem informada é inválida.");
        }
        if (precoRota.getMunicipioDestino() == null || precoRota.getMunicipioDestino().isEmpty()) {
            throw new CadastroException("O município de destino é obrigatório.");
        }
        if (precoRota.getUfDestino() == null || precoRota.getUfDestino().isEmpty()) {
            throw new CadastroException("A UF de destino é obrigatória.");
        }
        if (!UfUtil.validarUf(precoRota.getUfDestino())) {
            throw new CadastroException("A UF de destino informada é inválida.");
        }
        if (precoRota.getValorBase() == null
                || precoRota.getValorBase().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CadastroException("O valor base deve ser maior que zero.");
        }
        if (precoRota.getValorPorKg() != null
                && precoRota.getValorPorKg().compareTo(BigDecimal.ZERO) < 0) {
            throw new CadastroException("O valor por KG não pode ser negativo.");
        }
    }

    private void validarRota(PrecoRota precoRota) throws CadastroException {
        if (precoRota.getMunicipioOrigem().equalsIgnoreCase(precoRota.getMunicipioDestino())
                && precoRota.getUfOrigem().equalsIgnoreCase(precoRota.getUfDestino())) {
            throw new CadastroException("A origem e o destino não podem ser iguais.");
        }
    }
}