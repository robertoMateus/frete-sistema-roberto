package br.com.gwfrete.model;

public enum TipoVeiculo {
    TRUCK,
    CARRETA,
    VAN,
    UTILITARIO;

    public static TipoVeiculo fromString(String tipo) {
        for (TipoVeiculo t : TipoVeiculo.values()) {
            if (t.name().equalsIgnoreCase(tipo)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de veículo inválido: " + tipo);
    }
}
