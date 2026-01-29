package br.com.bank.voting.application.port.out;

import br.com.bank.voting.domain.model.Vote;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VoteRepositoryPort {
    Vote save(Vote vote);
    Optional<Vote> findByAgendaIdAndCpf(UUID agendaId, String cpf);
    List<Vote> findAllByAgendaId(UUID agendaId);
}

