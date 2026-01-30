package br.com.bank.voting.adapters.in.web.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ErrorResponse Tests")
class ErrorResponseTest {

    @Test
    @DisplayName("Deve criar ErrorResponse com código e mensagem")
    void shouldCreateErrorResponseWithCodeAndMessage() {
        ErrorResponse error = new ErrorResponse("ERROR_CODE", "Error message");

        assertEquals("ERROR_CODE", error.code());
        assertEquals("Error message", error.message());
        assertNotNull(error.timestamp());
    }

    @Test
    @DisplayName("Deve criar ErrorResponse com timestamp automático")
    void shouldCreateErrorResponseWithAutomaticTimestamp() {
        LocalDateTime before = LocalDateTime.now();
        ErrorResponse error = new ErrorResponse("ERROR_CODE", "Error message");
        LocalDateTime after = LocalDateTime.now();

        assertNotNull(error.timestamp());
        assertTrue(error.timestamp().isAfter(before.minusSeconds(1)));
        assertTrue(error.timestamp().isBefore(after.plusSeconds(1)));
    }

    @Test
    @DisplayName("Deve criar ErrorResponse com todos os parâmetros")
    void shouldCreateErrorResponseWithAllParameters() {
        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponse error = new ErrorResponse("ERROR_CODE", "Error message", timestamp);

        assertEquals("ERROR_CODE", error.code());
        assertEquals("Error message", error.message());
        assertEquals(timestamp, error.timestamp());
    }

    @Test
    @DisplayName("Deve permitir mensagem vazia")
    void shouldAllowEmptyMessage() {
        ErrorResponse error = new ErrorResponse("ERROR_CODE", "");

        assertEquals("ERROR_CODE", error.code());
        assertEquals("", error.message());
    }

    @Test
    @DisplayName("Deve permitir código vazio")
    void shouldAllowEmptyCode() {
        ErrorResponse error = new ErrorResponse("", "Error message");

        assertEquals("", error.code());
        assertEquals("Error message", error.message());
    }
}


