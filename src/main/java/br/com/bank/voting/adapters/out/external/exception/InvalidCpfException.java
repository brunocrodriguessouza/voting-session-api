package br.com.bank.voting.adapters.out.external.exception;

/**
 * Exceção lançada quando um CPF é inválido (API externa retorna 404).
 */
public class InvalidCpfException extends RuntimeException {
    
    public InvalidCpfException(String cpf) {
        super("Invalid CPF: " + maskCpf(cpf));
    }
    
    private static String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 4) {
            return "***";
        }
        return "***" + cpf.substring(cpf.length() - 4);
    }
}

