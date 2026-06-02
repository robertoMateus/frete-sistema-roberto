package br.com.gwfrete.model;

public enum TipoOcorrencia {
    SAIDA_PATIO,
    EM_ROTA,
    TENTATIVA_ENTREGA,
    ENTREGA_REALIZADA,
    AVARIA,
    EXTRAVIO,
    OUTROS;

    public static TipoOcorrencia fromString(String tipo) {
        for (TipoOcorrencia t : TipoOcorrencia.values()) {
            if (t.name().equalsIgnoreCase(tipo)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de ocorrência inválido: " + tipo);
    }
}
