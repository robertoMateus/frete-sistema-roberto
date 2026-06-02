package br.com.gwfrete.model;

public enum StatusVeiculo {
    DISPONIVEL,
    EM_VIAGEM,
    EM_MANUTENCAO;

    public static StatusVeiculo fromString(String status) {
        for (StatusVeiculo s : StatusVeiculo.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status de veículo inválido: " + status);
    }
}
