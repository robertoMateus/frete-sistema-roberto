package br.com.gwfrete.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Frete {
    private Long id;
    private String numeroFrete;
    private Cliente remetente;
    private Cliente destinatario;
    private Motorista motorista;
    private Veiculo veiculo;
    private String municipioOrigem;
    private String ufOrigem;
    private String municipioDestino;
    private String ufDestino;
    private String descricaoCarga;
    private BigDecimal pesoCarga;
    private Integer volumeCarga;
    private BigDecimal aliquotaIcms;
    private BigDecimal valorIcms;
    private BigDecimal valorTotal;
    private BigDecimal valorFrete;
    private LocalDateTime dataEmissao;
    private LocalDateTime dataPrevisaoEntrega;
    private LocalDateTime dataSaida;
    private LocalDateTime dataEntrega;
    private StatusFrete status;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Frete() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroFrete() {
        return numeroFrete;
    }

    public void setNumeroFrete(String numeroFrete) {
        this.numeroFrete = numeroFrete;
    }

    public Cliente getRemetente() {
        return remetente;
    }

    public void setRemetente(Cliente remetente) {
        this.remetente = remetente;
    }

    public Cliente getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Cliente destinatario) {
        this.destinatario = destinatario;
    }

    public Motorista getMotorista() {
        return motorista;
    }

    public void setMotorista(Motorista motorista) {
        this.motorista = motorista;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public String getMunicipioOrigem() {
        return municipioOrigem;
    }

    public void setMunicipioOrigem(String municipioOrigem) {
        this.municipioOrigem = municipioOrigem;
    }

    public String getUfOrigem() {
        return ufOrigem;
    }

    public void setUfOrigem(String ufOrigem) {
        this.ufOrigem = ufOrigem;
    }

    public String getMunicipioDestino() {
        return municipioDestino;
    }

    public void setMunicipioDestino(String municipioDestino) {
        this.municipioDestino = municipioDestino;
    }

    public String getUfDestino() {
        return ufDestino;
    }

    public void setUfDestino(String ufDestino) {
        this.ufDestino = ufDestino;
    }

    public String getDescricaoCarga() {
        return descricaoCarga;
    }

    public void setDescricaoCarga(String descricaoCarga) {
        this.descricaoCarga = descricaoCarga;
    }

    public BigDecimal getPesoCarga() {
        return pesoCarga;
    }

    public void setPesoCarga(BigDecimal pesoCarga) {
        this.pesoCarga = pesoCarga;
    }

    public Integer getVolumeCarga() {
        return volumeCarga;
    }

    public void setVolumeCarga(Integer volumeCarga) {
        this.volumeCarga = volumeCarga;
    }

    public LocalDateTime getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDateTime dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public LocalDateTime getDataPrevisaoEntrega() {
        return dataPrevisaoEntrega;
    }

    public void setDataPrevisaoEntrega(LocalDateTime dataPrevisaoEntrega) {
        this.dataPrevisaoEntrega = dataPrevisaoEntrega;
    }

    public BigDecimal getAliquotaIcms() {
        return aliquotaIcms;
    }

    public void setAliquotaIcms(BigDecimal aliquotaIcms) {
        this.aliquotaIcms = aliquotaIcms;
    }

    public BigDecimal getValorIcms() {
        return valorIcms;
    }

    public void setValorIcms(BigDecimal valorIcms) {
        this.valorIcms = valorIcms;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDateTime getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDateTime dataSaida) {
        this.dataSaida = dataSaida;
    }

    public LocalDateTime getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDateTime dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public StatusFrete getStatus() {
        return status;
    }

    public void setStatus(StatusFrete status) {
        this.status = status;
    }

    public BigDecimal getValorFrete() {
        return valorFrete;
    }

    public void setValorFrete(BigDecimal valorFrete) {
        this.valorFrete = valorFrete;
    }

    public String getDataPrevisaoEntregaFormatada() {
        if (dataPrevisaoEntrega == null)
            return "";
        return dataPrevisaoEntrega.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getDataEmissaoFormatada() {
        return dataEmissao != null ? dataEmissao.format(FORMATTER) : "";
    }

    public String getDataSaidaFormatada() {
        return dataSaida != null ? dataSaida.format(FORMATTER) : "";
    }

    public String getDataEntregaFormatada() {
        return dataEntrega != null ? dataEntrega.format(FORMATTER) : "";
    }

    public String getDataPrevisaoEntregaISO() {
        if (dataPrevisaoEntrega == null)
            return "";
        return dataPrevisaoEntrega.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
    }

}
