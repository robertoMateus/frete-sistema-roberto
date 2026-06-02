package br.com.gwfrete.model;

public enum StatusFrete {
    EMITIDO,
    SAIDA_CONFIRMADA,
    EM_TRANSITO,
    ENTREGUE,
    NAO_ENTREGUE,
    CANCELADO;

    public static StatusFrete fromString(String status) {
        for (StatusFrete s : StatusFrete.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status de frete inválido: " + status);
    }
}
