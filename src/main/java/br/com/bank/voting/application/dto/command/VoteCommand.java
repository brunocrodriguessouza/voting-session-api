package br.com.bank.voting.application.dto.command;

import br.com.bank.voting.domain.model.enums.VoteChoice;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record VoteCommand(
        @NotNull(message = "Agenda ID is required")
        UUID agendaId,
        
        @NotNull(message = "CPF is required")
        @Pattern(regexp = "\\d{11}", message = "CPF must contain exactly 11 digits")
        String cpf,
        
        @NotNull(message = "Vote choice is required")
        VoteChoice choice
) {
}


