package br.com.bank.voting.adapters.out.external.exception;

/**
 * Exceção lançada quando o serviço externo está indisponível (timeout, erro de rede, etc).
 */
public class ExternalServiceUnavailableException extends RuntimeException {
    
    public ExternalServiceUnavailableException(String message, Throwable cause) {
        super("External service unavailable: " + message, cause);
    }
}


