package br.com.bank.voting.application.dto.command;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OpenSessionCommand(
        @NotNull(message = "Agenda ID is required")
        UUID agendaId,
        
        Integer durationMinutes
) {
}

