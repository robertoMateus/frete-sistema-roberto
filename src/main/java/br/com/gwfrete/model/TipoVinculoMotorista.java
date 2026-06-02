package br.com.gwfrete.model;

public enum TipoVinculoMotorista {
    FUNCIONARIO,
    AGREGADO,
    TERCEIRO;
    public static TipoVinculoMotorista fromString(String tipo) {
        for (TipoVinculoMotorista t : TipoVinculoMotorista.values()) {
            if (t.name().equalsIgnoreCase(tipo)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de vínculo inválido: " + tipo);
    }
}
