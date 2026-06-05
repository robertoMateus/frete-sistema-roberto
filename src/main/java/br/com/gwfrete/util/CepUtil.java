package br.com.gwfrete.util;

public class CepUtil {

    private CepUtil() {}

    public static String limpar(String cep) {
        if (cep == null) return null;
        return cep.replaceAll("[^0-9]", "");
    }

    public static boolean validar(String cep) {
        if (cep == null) return false;
        String limpo = limpar(cep);
        return limpo.length() == 8;
    }
}