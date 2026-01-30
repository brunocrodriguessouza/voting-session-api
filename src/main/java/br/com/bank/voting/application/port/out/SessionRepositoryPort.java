package br.com.bank.voting.application.port.out;

import br.com.bank.voting.domain.model.VotingSession;

import java.util.Optional;
import java.util.UUID;

public interface SessionRepositoryPort {
    VotingSession save(VotingSession session);
    Optional<VotingSession> findByAgendaId(UUID agendaId);
}


