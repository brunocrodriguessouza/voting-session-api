package br.com.bank.voting.adapters.in.web.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        String code,
        String message,
        LocalDateTime timestamp
) {
    public ErrorResponse(String code, String message) {
        this(code, message, LocalDateTime.now());
    }
}

