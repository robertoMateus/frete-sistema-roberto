package br.com.gwfrete.model;

import java.time.LocalDateTime;

public class OcorrenciaFrete {
    private Long id;
    private TipoOcorrencia tipo;
    private LocalDateTime dataHoraOcorrencia;
    private String municipio;
    private String uf;
    private String descricao;
    private String nomeRecebedor;
    private String documentoRecebedor;
    private Frete frete;

    public OcorrenciaFrete() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoOcorrencia getTipo() {
        return tipo;
    }

    public void setTipo(TipoOcorrencia tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataHoraOcorrencia() {
        return dataHoraOcorrencia;
    }

    public void setDataHoraOcorrencia(LocalDateTime dataHoraOcorrencia) {
        this.dataHoraOcorrencia = dataHoraOcorrencia;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNomeRecebedor() {
        return nomeRecebedor;
    }

    public void setNomeRecebedor(String nomeRecebedor) {
        this.nomeRecebedor = nomeRecebedor;
    }

    public String getDocumentoRecebedor() {
        return documentoRecebedor;
    }

    public void setDocumentoRecebedor(String documentoRecebedor) {
        this.documentoRecebedor = documentoRecebedor;
    }

    public Frete getFrete() {
        return frete;
    }

    public void setFrete(Frete frete) {
        this.frete = frete;
    }
    
    

}
