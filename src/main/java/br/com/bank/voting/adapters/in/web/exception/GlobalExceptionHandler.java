package br.com.bank.voting.adapters.in.web.exception;

import br.com.bank.voting.adapters.out.external.exception.ExternalServiceUnavailableException;
import br.com.bank.voting.adapters.out.external.exception.InvalidCpfException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handler global para tratamento de exceções da aplicação.
 * Centraliza o tratamento de erros e retorna respostas padronizadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Trata exceções de argumentos inválidos (IllegalArgumentException).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("INVALID_REQUEST", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata exceções de estado inválido (IllegalStateException).
     * Mapeia diferentes tipos de erro para códigos HTTP apropriados.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;
        
        if (message.contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        } else if (message.contains("not eligible")) {
            status = HttpStatus.FORBIDDEN;
        }
        // Para "closed" e "already", mantém CONFLICT (já é o padrão)
        
        log.warn("Business rule violation: {}", message);
        ErrorResponse error = new ErrorResponse("BUSINESS_RULE_VIOLATION", message);
        return ResponseEntity.status(status).body(error);
    }

    /**
     * Trata exceções de validação de argumentos de método.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        
        log.warn("Validation error: {}", message);
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata exceções de violação de constraints de validação.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .findFirst()
                .orElse("Validation failed");
        
        log.warn("Constraint violation: {}", message);
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata exceções de CPF inválido (API externa retornou 404).
     */
    @ExceptionHandler(InvalidCpfException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCpfException(InvalidCpfException ex) {
        log.warn("Invalid CPF: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("INVALID_CPF", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata exceções de serviço externo indisponível.
     */
    @ExceptionHandler(ExternalServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceUnavailableException(ExternalServiceUnavailableException ex) {
        log.error("External service unavailable", ex);
        ErrorResponse error = new ErrorResponse("EXTERNAL_SERVICE_UNAVAILABLE", 
                "The external service is temporarily unavailable. Please try again later.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    /**
     * Trata exceções genéricas não tratadas.
     * Loga o erro completo para debug e retorna mensagem genérica ao cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        String message = ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred";
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

