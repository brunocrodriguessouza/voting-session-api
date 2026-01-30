package br.com.bank.voting.application.port.out;

import br.com.bank.voting.domain.model.Agenda;

import java.util.Optional;
import java.util.UUID;

public interface AgendaRepositoryPort {
    Agenda save(Agenda agenda);
    Optional<Agenda> findById(UUID id);
}


