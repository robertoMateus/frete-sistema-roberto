package br.com.gwfrete.model;

public enum StatusMotorista {

    ATIVO("Ativo"),
    INATIVO("Inativo"),
    SUSPENSO("Suspenso");

    private final String descricao;

    StatusMotorista(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusMotorista fromString(String status) {
        for (StatusMotorista s : StatusMotorista.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status de motorista inválido: " + status);
    }
}