package br.com.bank.voting.adapters.in.web.exception;

import br.com.bank.voting.adapters.out.external.exception.ExternalServiceUnavailableException;
import br.com.bank.voting.adapters.out.external.exception.InvalidCpfException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Deve tratar IllegalArgumentException com BAD_REQUEST")
    void shouldHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_REQUEST", response.getBody().code());
        assertEquals("Invalid argument", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("Deve tratar IllegalStateException com NOT_FOUND quando mensagem contém 'not found'")
    void shouldHandleIllegalStateExceptionWithNotFound() {
        IllegalStateException ex = new IllegalStateException("Session not found");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalStateException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("BUSINESS_RULE_VIOLATION", response.getBody().code());
        assertEquals("Session not found", response.getBody().message());
    }

    @ParameterizedTest
    @CsvSource({
        "Session is closed, 409",
        "Session already open, 409",
        "Associate is not eligible, 403"
    })
    @DisplayName("Deve tratar IllegalStateException com status HTTP apropriado")
    void shouldHandleIllegalStateExceptionWithAppropriateStatus(String message, int expectedStatusCode) {
        IllegalStateException ex = new IllegalStateException(message);

        ResponseEntity<ErrorResponse> response = handler.handleIllegalStateException(ex);

        assertEquals(expectedStatusCode, response.getStatusCode().value());
        assertEquals("BUSINESS_RULE_VIOLATION", response.getBody().code());
    }

    @Test
    @DisplayName("Deve tratar IllegalStateException com CONFLICT como padrão")
    void shouldHandleIllegalStateExceptionWithDefaultConflict() {
        IllegalStateException ex = new IllegalStateException("Some other error");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalStateException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("BUSINESS_RULE_VIOLATION", response.getBody().code());
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException")
    void shouldHandleMethodArgumentNotValidException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "title", "Title is required");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VALIDATION_ERROR", response.getBody().code());
        assertTrue(response.getBody().message().contains("title"));
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException sem field errors")
    void shouldHandleMethodArgumentNotValidExceptionWithoutFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VALIDATION_ERROR", response.getBody().code());
        assertEquals("Validation failed", response.getBody().message());
    }

    @Test
    @DisplayName("Deve tratar ConstraintViolationException")
    void shouldHandleConstraintViolationException() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);

        when(ex.getConstraintViolations()).thenReturn(Set.of(violation));
        when(violation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn("cpf");
        when(violation.getMessage()).thenReturn("CPF is required");

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VALIDATION_ERROR", response.getBody().code());
        assertTrue(response.getBody().message().contains("cpf"));
    }

    @Test
    @DisplayName("Deve tratar ConstraintViolationException sem violações")
    void shouldHandleConstraintViolationExceptionWithoutViolations() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);

        when(ex.getConstraintViolations()).thenReturn(Collections.emptySet());

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VALIDATION_ERROR", response.getBody().code());
        assertEquals("Validation failed", response.getBody().message());
    }

    @Test
    @DisplayName("Deve tratar InvalidCpfException")
    void shouldHandleInvalidCpfException() {
        InvalidCpfException ex = new InvalidCpfException("12345678901");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidCpfException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_CPF", response.getBody().code());
        assertNotNull(response.getBody().message());
    }

    @Test
    @DisplayName("Deve tratar ExternalServiceUnavailableException")
    void shouldHandleExternalServiceUnavailableException() {
        ExternalServiceUnavailableException ex = new ExternalServiceUnavailableException("Service error", 
                new RuntimeException("Connection timeout"));

        ResponseEntity<ErrorResponse> response = handler.handleExternalServiceUnavailableException(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("EXTERNAL_SERVICE_UNAVAILABLE", response.getBody().code());
        assertTrue(response.getBody().message().contains("temporarily unavailable"));
    }

    @Test
    @DisplayName("Deve tratar Exception genérica")
    void shouldHandleGenericException() {
        Exception ex = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("INTERNAL_ERROR", response.getBody().code());
        assertEquals("Unexpected error", response.getBody().message());
    }

    @Test
    @DisplayName("Deve tratar Exception genérica sem mensagem")
    void shouldHandleGenericExceptionWithoutMessage() {
        Exception ex = new RuntimeException();

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("INTERNAL_ERROR", response.getBody().code());
        assertEquals("An unexpected error occurred", response.getBody().message());
    }
}


