package br.com.gwfrete.model;

public enum StatusCliente {

    ATIVO("Ativo"),
    INATIVO("Inativo");

    private final String descricao;

    StatusCliente(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusCliente fromString(String status) {
        for (StatusCliente s : StatusCliente.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + status);
    }
}