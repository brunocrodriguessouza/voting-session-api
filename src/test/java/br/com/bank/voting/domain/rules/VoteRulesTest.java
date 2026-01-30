package br.com.bank.voting.domain.rules;

import br.com.bank.voting.domain.model.Vote;
import br.com.bank.voting.domain.model.enums.VoteChoice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VoteRules Tests")
class VoteRulesTest {

    private UUID agendaId;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        agendaId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Deve retornar true quando associado já votou")
    void shouldReturnTrueWhenAssociateAlreadyVoted() {
        String cpf = "12345678901";
        List<Vote> votes = Arrays.asList(
            new Vote(UUID.randomUUID(), agendaId, cpf, VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "98765432109", VoteChoice.NO, LocalDateTime.now())
        );

        assertTrue(VoteRules.hasAlreadyVoted(votes, cpf));
    }

    @Test
    @DisplayName("Deve retornar false quando associado não votou")
    void shouldReturnFalseWhenAssociateDidNotVote() {
        String cpf = "12345678901";
        List<Vote> votes = Arrays.asList(
            new Vote(UUID.randomUUID(), agendaId, "98765432109", VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "11111111111", VoteChoice.NO, LocalDateTime.now())
        );

        assertFalse(VoteRules.hasAlreadyVoted(votes, cpf));
    }

    @Test
    @DisplayName("Deve retornar false quando lista de votos estiver vazia")
    void shouldReturnFalseWhenVoteListIsEmpty() {
        String cpf = "12345678901";
        List<Vote> votes = Collections.emptyList();

        assertFalse(VoteRules.hasAlreadyVoted(votes, cpf));
    }

    @Test
    @DisplayName("Deve contar corretamente votos SIM")
    void shouldCountYesVotesCorrectly() {
        List<Vote> votes = Arrays.asList(
            new Vote(UUID.randomUUID(), agendaId, "11111111111", VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "22222222222", VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "33333333333", VoteChoice.NO, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "44444444444", VoteChoice.YES, LocalDateTime.now())
        );

        long count = VoteRules.countVotesByChoice(votes, VoteChoice.YES);

        assertEquals(3, count);
    }

    @Test
    @DisplayName("Deve contar corretamente votos NÃO")
    void shouldCountNoVotesCorrectly() {
        List<Vote> votes = Arrays.asList(
            new Vote(UUID.randomUUID(), agendaId, "11111111111", VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "22222222222", VoteChoice.NO, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "33333333333", VoteChoice.NO, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "44444444444", VoteChoice.NO, LocalDateTime.now())
        );

        long count = VoteRules.countVotesByChoice(votes, VoteChoice.NO);

        assertEquals(3, count);
    }

    @Test
    @DisplayName("Deve retornar zero quando não houver votos do tipo especificado")
    void shouldReturnZeroWhenNoVotesOfSpecifiedChoice() {
        List<Vote> votes = Arrays.asList(
            new Vote(UUID.randomUUID(), agendaId, "11111111111", VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "22222222222", VoteChoice.YES, LocalDateTime.now())
        );

        long count = VoteRules.countVotesByChoice(votes, VoteChoice.NO);

        assertEquals(0, count);
    }

    @Test
    @DisplayName("Deve retornar zero quando lista estiver vazia")
    void shouldReturnZeroWhenListIsEmpty() {
        List<Vote> votes = Collections.emptyList();

        long yesCount = VoteRules.countVotesByChoice(votes, VoteChoice.YES);
        long noCount = VoteRules.countVotesByChoice(votes, VoteChoice.NO);

        assertEquals(0, yesCount);
        assertEquals(0, noCount);
    }

    @Test
    @DisplayName("Deve contar corretamente quando todos os votos forem do mesmo tipo")
    void shouldCountCorrectlyWhenAllVotesAreSameType() {
        List<Vote> votes = Arrays.asList(
            new Vote(UUID.randomUUID(), agendaId, "11111111111", VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "22222222222", VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "33333333333", VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "44444444444", VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "55555555555", VoteChoice.YES, LocalDateTime.now())
        );

        long count = VoteRules.countVotesByChoice(votes, VoteChoice.YES);

        assertEquals(5, count);
    }

    @Test
    @DisplayName("Deve retornar true quando CPF está no primeiro voto")
    void shouldReturnTrueWhenCpfIsInFirstVote() {
        String cpf = "12345678901";
        List<Vote> votes = Arrays.asList(
            new Vote(UUID.randomUUID(), agendaId, cpf, VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "98765432109", VoteChoice.NO, LocalDateTime.now())
        );

        assertTrue(VoteRules.hasAlreadyVoted(votes, cpf));
    }

    @Test
    @DisplayName("Deve retornar true quando CPF está no último voto")
    void shouldReturnTrueWhenCpfIsInLastVote() {
        String cpf = "12345678901";
        List<Vote> votes = Arrays.asList(
            new Vote(UUID.randomUUID(), agendaId, "98765432109", VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, cpf, VoteChoice.NO, LocalDateTime.now())
        );

        assertTrue(VoteRules.hasAlreadyVoted(votes, cpf));
    }

    @Test
    @DisplayName("Deve retornar true quando CPF está no meio da lista")
    void shouldReturnTrueWhenCpfIsInMiddleOfList() {
        String cpf = "12345678901";
        List<Vote> votes = Arrays.asList(
            new Vote(UUID.randomUUID(), agendaId, "11111111111", VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, cpf, VoteChoice.NO, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "22222222222", VoteChoice.YES, LocalDateTime.now())
        );

        assertTrue(VoteRules.hasAlreadyVoted(votes, cpf));
    }
}


