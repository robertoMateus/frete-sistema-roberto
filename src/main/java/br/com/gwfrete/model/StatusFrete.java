package br.com.gwfrete.model;

public enum StatusFrete {

    EMITIDO("Emitido"),
    SAIDA_CONFIRMADA("Saída Confirmada"),
    EM_TRANSITO("Em Trânsito"),
    ENTREGUE("Entregue"),
    NAO_ENTREGUE("Não Entregue"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusFrete(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusFrete fromString(String status) {
        for (StatusFrete s : StatusFrete.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status de frete inválido: " + status);
    }
}