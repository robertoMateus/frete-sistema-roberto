package br.com.gwfrete.exception;

public class OcorrenciaException extends FreteException {
    public OcorrenciaException(String message) {
        super(message);
    }

    public OcorrenciaException(String message, Throwable causa) {
        super(message, causa);
    }
    
}
