package br.com.bank.voting.application.service;

import br.com.bank.voting.application.dto.command.OpenSessionCommand;
import br.com.bank.voting.application.dto.result.SessionOpenedResult;
import br.com.bank.voting.application.port.in.OpenSessionUseCase;
import br.com.bank.voting.application.port.out.AgendaRepositoryPort;
import br.com.bank.voting.application.port.out.SessionRepositoryPort;
import br.com.bank.voting.domain.model.VotingSession;
import br.com.bank.voting.domain.model.enums.SessionStatus;
import br.com.bank.voting.domain.rules.SessionRules;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OpenSessionService implements OpenSessionUseCase {

    private static final int DEFAULT_DURATION_MINUTES = 1;

    private final SessionRepositoryPort sessionRepository;
    private final AgendaRepositoryPort agendaRepository;

    public OpenSessionService(SessionRepositoryPort sessionRepository, AgendaRepositoryPort agendaRepository) {
        this.sessionRepository = sessionRepository;
        this.agendaRepository = agendaRepository;
    }

    @Override
    @Transactional
    public SessionOpenedResult open(OpenSessionCommand command) {
        agendaRepository.findById(command.agendaId())
                .orElseThrow(() -> new IllegalArgumentException("Agenda not found: " + command.agendaId()));

        sessionRepository.findByAgendaId(command.agendaId())
                .ifPresent(session -> {
                    throw new IllegalStateException("Session already open for agenda: " + command.agendaId());
                });

        int durationMinutes;
        if (command.durationMinutes() != null) {
            if (command.durationMinutes() <= 0) {
                throw new IllegalArgumentException("Duration must be positive");
            }
            durationMinutes = command.durationMinutes();
        } else {
            durationMinutes = DEFAULT_DURATION_MINUTES;
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

        return new SessionOpenedResult(
                saved.getId(),
                saved.getAgendaId(),
                saved.getOpenedAt(),
                saved.getClosesAt(),
                SessionStatus.OPEN
        );
    }
}

