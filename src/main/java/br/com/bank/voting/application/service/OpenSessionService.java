package br.com.bank.voting.application.service;

import br.com.bank.voting.application.dto.command.OpenSessionCommand;
import br.com.bank.voting.application.dto.result.SessionOpenedResult;
import br.com.bank.voting.application.port.in.OpenSessionUseCase;
import br.com.bank.voting.application.port.out.AgendaRepositoryPort;
import br.com.bank.voting.application.port.out.SessionRepositoryPort;
import br.com.bank.voting.domain.model.VotingSession;
import br.com.bank.voting.domain.model.enums.SessionStatus;
import br.com.bank.voting.domain.rules.SessionRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service responsável por abrir sessões de votação para pautas.
 * Implementa o caso de uso de abertura de sessões seguindo a arquitetura hexagonal.
 */
@Service
public class OpenSessionService implements OpenSessionUseCase {

    private static final Logger log = LoggerFactory.getLogger(OpenSessionService.class);
    private static final int DEFAULT_DURATION_MINUTES = 1;

    private final SessionRepositoryPort sessionRepository;
    private final AgendaRepositoryPort agendaRepository;

    public OpenSessionService(SessionRepositoryPort sessionRepository, AgendaRepositoryPort agendaRepository) {
        this.sessionRepository = sessionRepository;
        this.agendaRepository = agendaRepository;
    }

    /**
     * Abre uma sessão de votação para uma pauta.
     * A sessão ficará aberta pelo tempo especificado ou 1 minuto por padrão.
     *
     * @param command comando contendo o ID da pauta e duração opcional em minutos
     * @return resultado com os dados da sessão aberta
     * @throws IllegalArgumentException se a pauta não existir ou a duração for inválida
     * @throws IllegalStateException se já existir uma sessão aberta para a pauta
     */
    @Override
    @Transactional
    public SessionOpenedResult open(OpenSessionCommand command) {
        log.info("Opening session for agenda: {}", command.agendaId());
        
        agendaRepository.findById(command.agendaId())
                .orElseThrow(() -> {
                    log.error("Agenda not found: {}", command.agendaId());
                    return new IllegalArgumentException("Agenda not found: " + command.agendaId());
                });

        sessionRepository.findByAgendaId(command.agendaId())
                .ifPresent(session -> {
                    log.error("Session already open for agenda: {}", command.agendaId());
                    throw new IllegalStateException("Session already open for agenda: " + command.agendaId());
                });

        int durationMinutes;
        if (command.durationMinutes() != null) {
            if (command.durationMinutes() <= 0) {
                log.error("Invalid duration: {}", command.durationMinutes());
                throw new IllegalArgumentException("Duration must be positive");
            }
            durationMinutes = command.durationMinutes();
            log.debug("Using custom duration: {} minutes", durationMinutes);
        } else {
            durationMinutes = DEFAULT_DURATION_MINUTES;
            log.debug("Using default duration: {} minute", durationMinutes);
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime closesAt = SessionRules.calculateClosingTime(now, durationMinutes);

        VotingSession session = new VotingSession(
                null,
                command.agendaId(),
                now,
                closesAt
        );

        VotingSession saved = sessionRepository.save(session);
        
        log.info("Session opened successfully. Session ID: {}, Closes at: {}", saved.getId(), closesAt);

        return new SessionOpenedResult(
                saved.getId(),
                saved.getAgendaId(),
                saved.getOpenedAt(),
                saved.getClosesAt(),
                SessionStatus.OPEN
        );
    }
}

