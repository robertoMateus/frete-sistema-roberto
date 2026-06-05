package br.com.gwfrete.util;

public class EmailUtil {

    private EmailUtil() {}

    public static boolean validar(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        email = email.trim();

        int arroba = email.indexOf('@');
        if (arroba <= 0) {
            return false;
        }

        String dominio = email.substring(arroba + 1);
        if (dominio.isEmpty()) {
            return false;
        }

        int ponto = dominio.lastIndexOf('.');
        if (ponto <= 0 || ponto == dominio.length() - 1) {
            return false;
        }

        return true;
    }
}