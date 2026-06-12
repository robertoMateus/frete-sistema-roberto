package br.com.gwfrete.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ManutencaoVeiculo {
    private Long id;
    private Veiculo veiculo;
    private TipoManutencao tipo;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private BigDecimal custo;

    public ManutencaoVeiculo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public TipoManutencao getTipo() {
        return tipo;
    }

    public void setTipo(TipoManutencao tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public BigDecimal getCusto() {
        return custo;
    }

    public void setCusto(BigDecimal custo) {
        this.custo = custo;
    }

    public String getDataInicioFormatada() {
        if (dataInicio == null)
            return "";
        return dataInicio.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getDataFimFormatada() {
        if (dataFim == null)
            return "Em andamento";
        return dataFim.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getDataInicioISO() {
        return dataInicio != null ? dataInicio.toString() : "";
    }

    public String getDataFimISO() {
        return dataFim != null ? dataFim.toString() : "";
    }

}
