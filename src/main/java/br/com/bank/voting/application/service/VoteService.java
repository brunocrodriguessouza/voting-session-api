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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service responsável por registrar votos dos associados em pautas.
 * Implementa o caso de uso de votação seguindo a arquitetura hexagonal.
 */
@Service
public class VoteService implements VoteUseCase {

    private static final Logger log = LoggerFactory.getLogger(VoteService.class);

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

    /**
     * Registra um voto de um associado em uma pauta.
     * Valida se a pauta existe, se a sessão está aberta, se o associado já votou
     * e se o associado é elegível para votar.
     *
     * @param command comando contendo o ID da pauta, CPF do associado e escolha (SIM/NÃO)
     * @throws IllegalArgumentException se a pauta não existir
     * @throws IllegalStateException se a sessão não existir, estiver fechada, o associado já votou ou não for elegível
     */
    @Override
    @Transactional
    public void vote(VoteCommand command) {
        log.info("Processing vote for agenda: {}, CPF: {}, Choice: {}", 
                command.agendaId(), maskCpf(command.cpf()), command.choice());
        
        agendaRepository.findById(command.agendaId())
                .orElseThrow(() -> {
                    log.error("Agenda not found: {}", command.agendaId());
                    return new IllegalArgumentException("Agenda not found: " + command.agendaId());
                });

        VotingSession session = sessionRepository.findByAgendaId(command.agendaId())
                .orElseThrow(() -> {
                    log.error("No session found for agenda: {}", command.agendaId());
                    return new IllegalStateException("No session found for agenda: " + command.agendaId());
                });

        LocalDateTime now = LocalDateTime.now();
        if (!SessionRules.isSessionOpen(session, now)) {
            log.error("Session is closed for agenda: {}", command.agendaId());
            throw new IllegalStateException("Session is closed for agenda: " + command.agendaId());
        }

        voteRepository.findByAgendaIdAndCpf(command.agendaId(), command.cpf())
                .ifPresent(vote -> {
                    log.error("Associate already voted for agenda: {}, CPF: {}", 
                            command.agendaId(), maskCpf(command.cpf()));
                    throw new IllegalStateException("Associate already voted for this agenda");
                });

        if (!voterEligibilityPort.isEligibleToVote(command.cpf())) {
            log.error("Associate is not eligible to vote. CPF: {}", maskCpf(command.cpf()));
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
        log.info("Vote registered successfully for agenda: {}, CPF: {}", 
                command.agendaId(), maskCpf(command.cpf()));
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 4) {
            return "***";
        }
        return "***" + cpf.substring(cpf.length() - 4);
    }
}

