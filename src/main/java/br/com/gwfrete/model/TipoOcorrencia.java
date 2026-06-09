package br.com.gwfrete.model;

public enum TipoOcorrencia {
    SAIDA_PATIO("Saida do pátio"),
    EM_ROTA("Em rota"),
    TENTATIVA_ENTREGA("Tentativa de entrega"),
    ENTREGA_REALIZADA("Entrega realizada"),
    AVARIA("Avaria"),
    EXTRAVIO("Extravio"),
    OUTROS("Outros");

    private final String descricao;

    TipoOcorrencia(String descricao){
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }


    public static TipoOcorrencia fromString(String tipo) {
        for (TipoOcorrencia t : TipoOcorrencia.values()) {
            if (t.name().equalsIgnoreCase(tipo)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de ocorrência inválido: " + tipo);
    }
}
