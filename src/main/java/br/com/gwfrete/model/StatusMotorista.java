package br.com.gwfrete.model;

public enum StatusMotorista {
    ATIVO,
    INATIVO,
    SUSPENSO;

    public static StatusMotorista fromString(String status) {
        for (StatusMotorista s : StatusMotorista.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + status);
    }
}
