package br.com.gwfrete.exception;

public class FreteException extends NegocioException {
    public FreteException(String message) {
        super(message);
    }

    public FreteException(String message, Throwable causa) {
        super(message, causa);
    }
    
}
