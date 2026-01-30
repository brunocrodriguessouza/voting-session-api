package br.com.bank.voting.domain.rules;

import br.com.bank.voting.domain.model.Vote;
import br.com.bank.voting.domain.model.enums.VoteChoice;

import java.util.List;

/**
 * Regras de negócio relacionadas a votos.
 * Contém lógica pura de domínio, sem dependências de frameworks.
 */
public class VoteRules {

    /**
     * Verifica se um associado já votou em uma lista de votos.
     *
     * @param votes lista de votos a ser verificada
     * @param cpf CPF do associado
     * @return true se o associado já votou, false caso contrário
     */
    public static boolean hasAlreadyVoted(List<Vote> votes, String cpf) {
        return votes.stream()
                .anyMatch(vote -> vote.getCpf().equals(cpf));
    }

    /**
     * Conta quantos votos de um determinado tipo (SIM ou NÃO) existem na lista.
     *
     * @param votes lista de votos
     * @param choice tipo de voto a ser contado (YES ou NO)
     * @return quantidade de votos do tipo especificado
     */
    public static long countVotesByChoice(List<Vote> votes, VoteChoice choice) {
        return votes.stream()
                .filter(vote -> vote.getChoice() == choice)
                .count();
    }
}

