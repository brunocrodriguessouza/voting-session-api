package br.com.bank.voting.application.service;

import br.com.bank.voting.application.dto.command.VoteCommand;
import br.com.bank.voting.application.port.in.VoteUseCase;
import br.com.bank.voting.application.port.out.AgendaRepositoryPort;
import br.com.bank.voting.application.port.out.SessionRepositoryPort;
import br.com.bank.voting.application.port.out.VoteRepositoryPort;
import br.com.bank.voting.application.port.out.VoterEligibilityPort;
import br.com.bank.voting.domain.model.Vote;
import br.com.bank.voting.domain.model.VotingSession;
import br.com.bank.voting.domain.rules.SessionRules;
import br.com.bank.voting.domain.rules.VoteRules;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VoteService implements VoteUseCase {

    private final VoteRepositoryPort voteRepository;
    private final SessionRepositoryPort sessionRepository;
    private final AgendaRepositoryPort agendaRepository;
    private final VoterEligibilityPort voterEligibilityPort;

    public VoteService(
            VoteRepositoryPort voteRepository,
            SessionRepositoryPort sessionRepository,
            AgendaRepositoryPort agendaRepository,
            VoterEligibilityPort voterEligibilityPort) {
        this.voteRepository = voteRepository;
        this.sessionRepository = sessionRepository;
        this.agendaRepository = agendaRepository;
        this.voterEligibilityPort = voterEligibilityPort;
    }

    @Override
    @Transactional
    public void vote(VoteCommand command) {
        agendaRepository.findById(command.agendaId())
                .orElseThrow(() -> new IllegalArgumentException("Agenda not found: " + command.agendaId()));

        VotingSession session = sessionRepository.findByAgendaId(command.agendaId())
                .orElseThrow(() -> new IllegalStateException("No session found for agenda: " + command.agendaId()));

        LocalDateTime now = LocalDateTime.now();
        if (!SessionRules.isSessionOpen(session, now)) {
            throw new IllegalStateException("Session is closed for agenda: " + command.agendaId());
        }

        voteRepository.findByAgendaIdAndCpf(command.agendaId(), command.cpf())
                .ifPresent(vote -> {
                    throw new IllegalStateException("Associate already voted for this agenda");
                });

        if (!voterEligibilityPort.isEligibleToVote(command.cpf())) {
            throw new IllegalStateException("Associate is not eligible to vote");
        }

        Vote vote = new Vote(
                null,
                command.agendaId(),
                command.cpf(),
                command.choice(),
                now
        );

        voteRepository.save(vote);
    }
}

