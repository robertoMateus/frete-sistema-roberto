package br.com.gwfrete.exception;

public class VeiculoException extends CadastroException {
    public VeiculoException(String message) {
        super(message);
    }

    public VeiculoException(String message, Throwable causa) {
        super(message, causa);
    }
    
}
