package br.com.gwfrete.util;

public class CnpjUtil {
    public static boolean validarCnpj(String cnpj) {
        if (cnpj == null){
            return false;
        }

        //Caso venha formatado
        cnpj = cnpj.replaceAll("[^0-9]", "");

        if (cnpj.length() != 14) {
            return false; // CNPJ deve conter 14 dígitos
        }

        //Verificar se todos os dígitos são iguais
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        // Pesos para cálculo do primeiro dígito verificador
        int[] pesosPrimeiro = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        //Validador de primeiro dígito
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesosPrimeiro[i];
        }
        int primeiroDigito = soma % 11 < 2 ? 0 : 11 - (soma % 11);
        if (primeiroDigito != Character.getNumericValue(cnpj.charAt(12))) {
            return false;
        }

        // Pesos para cálculo do segundo dígito verificador
        int[] pesosSegundo = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        //Validador de segundo dígito
        soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesosSegundo[i];
        }
        int segundoDigito = soma % 11 < 2 ? 0 : 11 - (soma % 11);

        return segundoDigito == Character.getNumericValue(cnpj.charAt(13));
    }
}
