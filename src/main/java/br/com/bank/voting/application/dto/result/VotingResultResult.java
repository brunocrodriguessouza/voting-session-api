package br.com.bank.voting.application.dto.result;

import br.com.bank.voting.domain.model.enums.SessionStatus;
import br.com.bank.voting.domain.model.enums.VotingResult;

import java.util.UUID;

public record VotingResultResult(
        UUID agendaId,
        SessionStatus sessionStatus,
        Long yes,
        Long no,
        Long total,
        VotingResult result
) {
}


