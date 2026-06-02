package br.com.gwfrete.exception;

public class CadastroException extends NegocioException {
    public CadastroException(String message) {
        super(message);
    }

    public CadastroException(String message, Throwable causa) {
        super(message, causa);
    }
    
}
