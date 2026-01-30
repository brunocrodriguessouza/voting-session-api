package br.com.bank.voting.application.port.in;

import br.com.bank.voting.application.dto.result.VotingResultResult;

import java.util.UUID;

public interface GetResultUseCase {
    VotingResultResult getResult(UUID agendaId);
}


