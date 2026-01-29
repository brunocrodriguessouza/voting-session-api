package br.com.bank.voting.application.dto.result;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendaCreatedResult(
        UUID id,
        String title,
        LocalDateTime createdAt
) {
}

