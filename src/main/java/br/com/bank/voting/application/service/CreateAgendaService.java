package br.com.bank.voting.application.service;

import br.com.bank.voting.application.dto.command.CreateAgendaCommand;
import br.com.bank.voting.application.dto.result.AgendaCreatedResult;
import br.com.bank.voting.application.port.in.CreateAgendaUseCase;
import br.com.bank.voting.application.port.out.AgendaRepositoryPort;
import br.com.bank.voting.domain.model.Agenda;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service responsável por criar novas pautas de votação.
 * Implementa o caso de uso de criação de pautas seguindo a arquitetura hexagonal.
 */
@Service
public class CreateAgendaService implements CreateAgendaUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateAgendaService.class);

    private final AgendaRepositoryPort agendaRepository;

    public CreateAgendaService(AgendaRepositoryPort agendaRepository) {
        this.agendaRepository = agendaRepository;
    }

    /**
     * Cria uma nova pauta de votação.
     *
     * @param command comando contendo o título da pauta
     * @return resultado com os dados da pauta criada (ID, título e data de criação)
     */
    @Override
    @Transactional
    public AgendaCreatedResult create(CreateAgendaCommand command) {
        log.info("Creating new agenda with title: {}", command.title());
        
        Agenda agenda = new Agenda(
                null,
                command.title(),
                LocalDateTime.now()
        );

        Agenda saved = agendaRepository.save(agenda);
        
        log.info("Agenda created successfully with ID: {}", saved.getId());

        return new AgendaCreatedResult(
                saved.getId(),
                saved.getTitle(),
                saved.getCreatedAt()
        );
    }
}

