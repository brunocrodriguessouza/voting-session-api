package br.com.bank.voting.application.service;

import br.com.bank.voting.application.dto.result.VotingResultResult;
import br.com.bank.voting.application.port.in.GetResultUseCase;
import br.com.bank.voting.application.port.out.AgendaRepositoryPort;
import br.com.bank.voting.application.port.out.ResultPublisherPort;
import br.com.bank.voting.application.port.out.SessionRepositoryPort;
import br.com.bank.voting.application.port.out.VoteRepositoryPort;
import br.com.bank.voting.domain.model.Vote;
import br.com.bank.voting.domain.model.VotingSession;
import br.com.bank.voting.domain.model.enums.SessionStatus;
import br.com.bank.voting.domain.model.enums.VoteChoice;
import br.com.bank.voting.domain.model.enums.VotingResult;
import br.com.bank.voting.domain.rules.SessionRules;
import br.com.bank.voting.domain.rules.VoteRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service responsável por obter e contabilizar os resultados de uma votação.
 * Implementa o caso de uso de apuração de resultados seguindo a arquitetura hexagonal.
 */
@Service
public class GetResultService implements GetResultUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetResultService.class);

    private final AgendaRepositoryPort agendaRepository;
    private final SessionRepositoryPort sessionRepository;
    private final VoteRepositoryPort voteRepository;
    private final ResultPublisherPort resultPublisherPort;
    private final Set<UUID> publishedResults = Collections.synchronizedSet(new HashSet<>());

    public GetResultService(
            AgendaRepositoryPort agendaRepository,
            SessionRepositoryPort sessionRepository,
            VoteRepositoryPort voteRepository,
            ResultPublisherPort resultPublisherPort) {
        this.agendaRepository = agendaRepository;
        this.sessionRepository = sessionRepository;
        this.voteRepository = voteRepository;
        this.resultPublisherPort = resultPublisherPort;
    }

    /**
     * Obtém o resultado da votação de uma pauta.
     * Contabiliza os votos SIM e NÃO e determina se a pauta foi aprovada ou rejeitada.
     * Em caso de empate, a pauta é considerada rejeitada.
     *
     * @param agendaId ID da pauta
     * @return resultado contendo status da sessão, contagem de votos e resultado final
     * @throws IllegalArgumentException se a pauta não existir
     */
    @Override
    @Transactional(readOnly = true)
    public VotingResultResult getResult(UUID agendaId) {
        log.info("Getting voting result for agenda: {}", agendaId);
        
        agendaRepository.findById(agendaId)
                .orElseThrow(() -> {
                    log.error("Agenda not found: {}", agendaId);
                    return new IllegalArgumentException("Agenda not found: " + agendaId);
                });

        VotingSession session = sessionRepository.findByAgendaId(agendaId).orElse(null);
        LocalDateTime now = LocalDateTime.now();
        SessionStatus sessionStatus = (session != null && SessionRules.isSessionOpen(session, now))
                ? SessionStatus.OPEN
                : SessionStatus.CLOSED;

        List<Vote> votes = voteRepository.findAllByAgendaId(agendaId);

        long yesCount = VoteRules.countVotesByChoice(votes, VoteChoice.YES);
        long noCount = VoteRules.countVotesByChoice(votes, VoteChoice.NO);
        long total = votes.size();

        VotingResult result;
        if (yesCount > noCount) {
            result = VotingResult.APPROVED;
        } else {
            result = VotingResult.REJECTED;
        }

        log.info("Voting result for agenda {}: {} approved, {} rejected, total: {}, result: {}", 
                agendaId, yesCount, noCount, total, result);

        VotingResultResult votingResult = new VotingResultResult(
                agendaId,
                sessionStatus,
                yesCount,
                noCount,
                total,
                result
        );

        // Publica resultado na fila quando a sessão estiver fechada (Bônus 2)
        if (sessionStatus == SessionStatus.CLOSED && !publishedResults.contains(agendaId)) {
            try {
                resultPublisherPort.publishResult(votingResult);
                publishedResults.add(agendaId);
                log.info("Voting result published to message queue for agenda: {}", agendaId);
            } catch (Exception e) {
                log.error("Error publishing result to message queue for agenda: {}", agendaId, e);
                // Não lança exceção para não quebrar o fluxo principal
            }
        }

        return votingResult;
    }
}

