package br.com.bank.voting.adapters.out.external.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InvalidCpfException Tests")
class InvalidCpfExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com CPF válido e mascarar corretamente")
    void shouldCreateExceptionWithValidCpfAndMaskCorrectly() {
        String cpf = "12345678901";
        
        InvalidCpfException exception = new InvalidCpfException(cpf);
        
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Invalid CPF"));
        assertTrue(exception.getMessage().contains("***"));
        assertFalse(exception.getMessage().contains("12345678901"));
        assertTrue(exception.getMessage().contains("8901")); // últimos 4 dígitos
    }

    @Test
    @DisplayName("Deve mascarar CPF com menos de 4 dígitos")
    void shouldMaskCpfWithLessThan4Digits() {
        String cpf = "123";
        
        InvalidCpfException exception = new InvalidCpfException(cpf);
        
        assertTrue(exception.getMessage().contains("***"));
    }

    @Test
    @DisplayName("Deve tratar CPF null")
    void shouldHandleNullCpf() {
        InvalidCpfException exception = new InvalidCpfException(null);
        
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Invalid CPF"));
        assertTrue(exception.getMessage().contains("***"));
    }

    @Test
    @DisplayName("Deve tratar CPF vazio")
    void shouldHandleEmptyCpf() {
        InvalidCpfException exception = new InvalidCpfException("");
        
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Invalid CPF"));
    }

    @Test
    @DisplayName("Deve mascarar CPF com exatamente 4 dígitos")
    void shouldMaskCpfWithExactly4Digits() {
        String cpf = "1234";
        
        InvalidCpfException exception = new InvalidCpfException(cpf);
        
        assertTrue(exception.getMessage().contains("***1234"));
    }

    @Test
    @DisplayName("Deve mascarar CPF com mais de 4 dígitos")
    void shouldMaskCpfWithMoreThan4Digits() {
        String cpf = "12345678901234";
        
        InvalidCpfException exception = new InvalidCpfException(cpf);
        
        assertTrue(exception.getMessage().contains("***"));
        assertTrue(exception.getMessage().contains("1234")); // últimos 4 dígitos
        assertFalse(exception.getMessage().contains("12345678901234"));
    }
}


