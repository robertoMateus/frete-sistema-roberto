package br.com.gwfrete.model;

public enum TipoVinculoMotorista {
    FUNCIONARIO("Funcionário"),
    AGREGADO("Agregado"),
    TERCEIRO("Terceiro");

    private final String descricao;

    TipoVinculoMotorista(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoVinculoMotorista fromString(String tipo) {
        for (TipoVinculoMotorista t : TipoVinculoMotorista.values()) {
            if (t.name().equalsIgnoreCase(tipo)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de vínculo inválido: " + tipo);
    }
}