package br.com.gwfrete.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UfUtil {

    private UfUtil() {
    }

    public static final Set<String> UFS_VALIDAS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO",
                    "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI",
                    "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"
            ))
    );

    public static boolean validarUf(String uf) {
        if (uf == null || uf.trim().isEmpty()) {
            return false;
        }
        return UFS_VALIDAS.contains(uf.trim().toUpperCase());
    }

    public static String normalizar(String uf) {
        if (uf == null) {
            return null;
        }
        return uf.trim().toUpperCase();
    }
}