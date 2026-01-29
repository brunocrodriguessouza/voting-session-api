package br.com.bank.voting.application.service;

import br.com.bank.voting.application.dto.result.VotingResultResult;
import br.com.bank.voting.application.port.in.GetResultUseCase;
import br.com.bank.voting.application.port.out.AgendaRepositoryPort;
import br.com.bank.voting.application.port.out.SessionRepositoryPort;
import br.com.bank.voting.application.port.out.VoteRepositoryPort;
import br.com.bank.voting.domain.model.Vote;
import br.com.bank.voting.domain.model.VotingSession;
import br.com.bank.voting.domain.model.enums.SessionStatus;
import br.com.bank.voting.domain.model.enums.VoteChoice;
import br.com.bank.voting.domain.model.enums.VotingResult;
import br.com.bank.voting.domain.rules.SessionRules;
import br.com.bank.voting.domain.rules.VoteRules;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GetResultService implements GetResultUseCase {

    private final AgendaRepositoryPort agendaRepository;
    private final SessionRepositoryPort sessionRepository;
    private final VoteRepositoryPort voteRepository;

    public GetResultService(
            AgendaRepositoryPort agendaRepository,
            SessionRepositoryPort sessionRepository,
            VoteRepositoryPort voteRepository) {
        this.agendaRepository = agendaRepository;
        this.sessionRepository = sessionRepository;
        this.voteRepository = voteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public VotingResultResult getResult(UUID agendaId) {
        agendaRepository.findById(agendaId)
                .orElseThrow(() -> new IllegalArgumentException("Agenda not found: " + agendaId));

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

        return new VotingResultResult(
                agendaId,
                sessionStatus,
                yesCount,
                noCount,
                total,
                result
        );
    }
}

