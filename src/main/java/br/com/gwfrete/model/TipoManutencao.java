package br.com.gwfrete.model;

public enum TipoManutencao {
    PREVENTIVA("Preventiva"),
    CORRETIVA("Corretiva");

    private final String descricao;

    TipoManutencao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoManutencao fromString(String tipo) {
        for (TipoManutencao t : TipoManutencao.values()) {
            if (t.name().equalsIgnoreCase(tipo)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de manutenção inválido: " + tipo);
    }
}