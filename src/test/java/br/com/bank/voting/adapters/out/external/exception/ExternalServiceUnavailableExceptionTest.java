package br.com.bank.voting.adapters.out.external.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ExternalServiceUnavailableException Tests")
class ExternalServiceUnavailableExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem e causa")
    void shouldCreateExceptionWithMessageAndCause() {
        String message = "Connection timeout";
        RuntimeException cause = new RuntimeException("Network error");
        
        ExternalServiceUnavailableException exception = new ExternalServiceUnavailableException(message, cause);
        
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("External service unavailable"));
        assertTrue(exception.getMessage().contains(message));
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve criar exceção sem causa")
    void shouldCreateExceptionWithoutCause() {
        String message = "Service error";
        
        ExternalServiceUnavailableException exception = new ExternalServiceUnavailableException(message, null);
        
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("External service unavailable"));
        assertTrue(exception.getMessage().contains(message));
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem vazia")
    void shouldCreateExceptionWithEmptyMessage() {
        ExternalServiceUnavailableException exception = new ExternalServiceUnavailableException("", null);
        
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("External service unavailable"));
    }

    @Test
    @DisplayName("Deve criar exceção com causa de tipo diferente")
    void shouldCreateExceptionWithDifferentCauseType() {
        String message = "Service error";
        Exception cause = new Exception("Root cause");
        
        ExternalServiceUnavailableException exception = new ExternalServiceUnavailableException(message, cause);
        
        assertEquals(cause, exception.getCause());
        assertTrue(exception.getMessage().contains(message));
    }

    @Test
    @DisplayName("Deve preservar stack trace da causa")
    void shouldPreserveCauseStackTrace() {
        RuntimeException cause = new RuntimeException("Original error");
        cause.fillInStackTrace();
        
        ExternalServiceUnavailableException exception = new ExternalServiceUnavailableException("Error", cause);
        
        assertNotNull(exception.getCause());
        assertNotNull(exception.getCause().getStackTrace());
        assertTrue(exception.getCause().getStackTrace().length > 0);
    }
}

