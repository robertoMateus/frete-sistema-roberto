package br.com.gwfrete.util;

import java.time.LocalDateTime;

public class FreteUtil {
    public static String formatarNumeroFrete(int sequencial) {

        int ano = LocalDateTime.now().getYear();
        return String.format("FRT-%d-%05d", ano, sequencial);
    }
}
