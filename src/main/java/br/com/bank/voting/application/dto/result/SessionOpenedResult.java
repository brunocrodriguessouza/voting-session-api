package br.com.bank.voting.application.dto.result;

import br.com.bank.voting.domain.model.enums.SessionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessionOpenedResult(
        UUID sessionId,
        UUID agendaId,
        LocalDateTime openedAt,
        LocalDateTime closesAt,
        SessionStatus status
) {
}


