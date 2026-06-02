package br.com.gwfrete.exception;

public class NegocioException extends Exception {
    public NegocioException(String message) {
        super(message);
    }

    public NegocioException(String message, Throwable causa) {
        super(message, causa);
    }
    
}
