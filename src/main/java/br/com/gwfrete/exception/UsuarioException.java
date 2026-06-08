package br.com.gwfrete.exception;

public class UsuarioException extends NegocioException {

    public UsuarioException(String message) {
        super(message);
    }

    public UsuarioException(String message, Throwable causa) {
        super(message, causa);
    }
}