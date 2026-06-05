package br.com.gwfrete.model;

public enum StatusVeiculo {

    DISPONIVEL("Disponível"),
    EM_VIAGEM("Em Viagem"),
    EM_MANUTENCAO("Em Manutenção");

    private final String descricao;

    StatusVeiculo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusVeiculo fromString(String status) {
        for (StatusVeiculo s : StatusVeiculo.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status de veículo inválido: " + status);
    }
}