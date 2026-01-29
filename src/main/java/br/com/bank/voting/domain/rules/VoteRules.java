package br.com.bank.voting.domain.rules;

import br.com.bank.voting.domain.model.Vote;
import br.com.bank.voting.domain.model.enums.VoteChoice;

import java.util.List;

/**
 * Regras de negócio relacionadas a votos.
 */
public class VoteRules {

    public static boolean hasAlreadyVoted(List<Vote> votes, String cpf) {
        return votes.stream()
                .anyMatch(vote -> vote.getCpf().equals(cpf));
    }

    public static long countVotesByChoice(List<Vote> votes, VoteChoice choice) {
        return votes.stream()
                .filter(vote -> vote.getChoice() == choice)
                .count();
    }
}

