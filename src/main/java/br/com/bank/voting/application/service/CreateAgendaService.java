package br.com.bank.voting.application.service;

import br.com.bank.voting.application.dto.command.CreateAgendaCommand;
import br.com.bank.voting.application.dto.result.AgendaCreatedResult;
import br.com.bank.voting.application.port.in.CreateAgendaUseCase;
import br.com.bank.voting.application.port.out.AgendaRepositoryPort;
import br.com.bank.voting.domain.model.Agenda;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CreateAgendaService implements CreateAgendaUseCase {

    private final AgendaRepositoryPort agendaRepository;

    public CreateAgendaService(AgendaRepositoryPort agendaRepository) {
        this.agendaRepository = agendaRepository;
    }

    @Override
    @Transactional
    public AgendaCreatedResult create(CreateAgendaCommand command) {
        Agenda agenda = new Agenda(
                null,  // ID será gerado pelo JPA
                command.title(),
                LocalDateTime.now()
        );

        Agenda saved = agendaRepository.save(agenda);

        return new AgendaCreatedResult(
                saved.getId(),
                saved.getTitle(),
                saved.getCreatedAt()
        );
    }
}

