package br.com.gwfrete.exception;

public class ClienteException extends CadastroException {
    public ClienteException(String message) {
        super(message);
    }

    public ClienteException(String message, Throwable causa) {
        super(message, causa);
    }
    
}
