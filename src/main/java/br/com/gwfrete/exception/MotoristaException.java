package br.com.gwfrete.exception;

public class MotoristaException extends CadastroException {
    public MotoristaException(String message) {
        super(message);
    }

    public MotoristaException(String message, Throwable causa) {
        super(message, causa);
    }
    
}
