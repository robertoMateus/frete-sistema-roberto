package br.com.gwfrete.model;

public enum CategoriaCnh {
    A,
    B,
    C,
    D,
    E;

    public static CategoriaCnh fromString(String categoria) {
        for (CategoriaCnh c : CategoriaCnh.values()) {
            if (c.name().equalsIgnoreCase(categoria)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Categoria CNH inválida: " + categoria);
    }
}