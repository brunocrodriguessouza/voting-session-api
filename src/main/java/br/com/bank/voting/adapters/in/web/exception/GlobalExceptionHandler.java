package br.com.bank.voting.adapters.in.web.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse("INVALID_REQUEST", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;
        
        if (message.contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        } else if (message.contains("closed")) {
            status = HttpStatus.CONFLICT;
        } else if (message.contains("already")) {
            status = HttpStatus.CONFLICT;
        } else if (message.contains("not eligible")) {
            status = HttpStatus.FORBIDDEN;
        }
        
        ErrorResponse error = new ErrorResponse("BUSINESS_RULE_VIOLATION", message);
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .findFirst()
                .orElse("Validation failed");
        
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // Log do erro completo para debug
        ex.printStackTrace();
        String message = ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred";
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

