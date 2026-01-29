package br.com.bank.voting.application.dto.command;

import jakarta.validation.constraints.NotBlank;

public record CreateAgendaCommand(
        @NotBlank(message = "Title is required")
        String title
) {
}

