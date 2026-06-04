package br.com.gwfrete.model;

public enum TipoManutencao {
    PREVENTIVA,
    CORRETIVA;

    public static TipoManutencao fromString(String tipo) {
        for (TipoManutencao t : TipoManutencao.values()) {
            if (t.name().equalsIgnoreCase(tipo)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de manutenção inválido: " + tipo);
    }
}
