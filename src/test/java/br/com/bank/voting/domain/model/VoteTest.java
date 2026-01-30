package br.com.bank.voting.domain.model;

import br.com.bank.voting.domain.model.enums.VoteChoice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Vote Tests")
class VoteTest {

    @Test
    @DisplayName("Deve criar voto com todos os campos")
    void shouldCreateVoteWithAllFields() {
        UUID id = UUID.randomUUID();
        UUID agendaId = UUID.randomUUID();
        String cpf = "12345678901";
        VoteChoice choice = VoteChoice.YES;
        LocalDateTime createdAt = LocalDateTime.now();

        Vote vote = new Vote(id, agendaId, cpf, choice, createdAt);

        assertEquals(id, vote.getId());
        assertEquals(agendaId, vote.getAgendaId());
        assertEquals(cpf, vote.getCpf());
        assertEquals(choice, vote.getChoice());
        assertEquals(createdAt, vote.getCreatedAt());
    }

    @Test
    @DisplayName("Deve criar voto com ID null")
    void shouldCreateVoteWithNullId() {
        UUID agendaId = UUID.randomUUID();
        String cpf = "98765432100";
        VoteChoice choice = VoteChoice.NO;
        LocalDateTime createdAt = LocalDateTime.now();

        Vote vote = new Vote(null, agendaId, cpf, choice, createdAt);

        assertNull(vote.getId());
        assertEquals(agendaId, vote.getAgendaId());
        assertEquals(cpf, vote.getCpf());
        assertEquals(choice, vote.getChoice());
        assertEquals(createdAt, vote.getCreatedAt());
    }

    @Test
    @DisplayName("Deve criar voto com escolha YES")
    void shouldCreateVoteWithYesChoice() {
        UUID agendaId = UUID.randomUUID();
        String cpf = "11111111111";
        VoteChoice choice = VoteChoice.YES;

        Vote vote = new Vote(null, agendaId, cpf, choice, LocalDateTime.now());

        assertEquals(VoteChoice.YES, vote.getChoice());
    }

    @Test
    @DisplayName("Deve criar voto com escolha NO")
    void shouldCreateVoteWithNoChoice() {
        UUID agendaId = UUID.randomUUID();
        String cpf = "22222222222";
        VoteChoice choice = VoteChoice.NO;

        Vote vote = new Vote(null, agendaId, cpf, choice, LocalDateTime.now());

        assertEquals(VoteChoice.NO, vote.getChoice());
    }

    @Test
    @DisplayName("Deve retornar valores corretos dos getters")
    void shouldReturnCorrectValuesFromGetters() {
        UUID id = UUID.randomUUID();
        UUID agendaId = UUID.randomUUID();
        String cpf = "33333333333";
        VoteChoice choice = VoteChoice.YES;
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 30, 10, 0, 0);

        Vote vote = new Vote(id, agendaId, cpf, choice, createdAt);

        assertEquals(id, vote.getId());
        assertEquals(agendaId, vote.getAgendaId());
        assertEquals(cpf, vote.getCpf());
        assertEquals(choice, vote.getChoice());
        assertEquals(createdAt, vote.getCreatedAt());
    }
}

