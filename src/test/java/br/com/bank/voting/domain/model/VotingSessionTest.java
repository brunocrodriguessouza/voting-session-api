package br.com.bank.voting.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VotingSession Tests")
class VotingSessionTest {

    @Test
    @DisplayName("Deve criar sessão com todos os campos")
    void shouldCreateSessionWithAllFields() {
        UUID id = UUID.randomUUID();
        UUID agendaId = UUID.randomUUID();
        LocalDateTime openedAt = LocalDateTime.now();
        LocalDateTime closesAt = openedAt.plusMinutes(10);

        VotingSession session = new VotingSession(id, agendaId, openedAt, closesAt);

        assertEquals(id, session.getId());
        assertEquals(agendaId, session.getAgendaId());
        assertEquals(openedAt, session.getOpenedAt());
        assertEquals(closesAt, session.getClosesAt());
    }

    @Test
    @DisplayName("Deve retornar true quando sessão está aberta")
    void shouldReturnTrueWhenSessionIsOpen() {
        LocalDateTime now = LocalDateTime.now();
        VotingSession session = new VotingSession(
            UUID.randomUUID(),
            UUID.randomUUID(),
            now,
            now.plusMinutes(5)
        );

        assertTrue(session.isOpen(now));
        assertTrue(session.isOpen(now.plusMinutes(2)));
    }

    @Test
    @DisplayName("Deve retornar false quando sessão está fechada")
    void shouldReturnFalseWhenSessionIsClosed() {
        LocalDateTime now = LocalDateTime.now();
        VotingSession session = new VotingSession(
            UUID.randomUUID(),
            UUID.randomUUID(),
            now.minusMinutes(10),
            now.minusMinutes(5)
        );

        assertFalse(session.isOpen(now));
        assertFalse(session.isOpen(now.plusMinutes(1)));
    }

    @Test
    @DisplayName("Deve retornar false quando horário atual for igual ao horário de fechamento")
    void shouldReturnFalseWhenCurrentTimeEqualsClosingTime() {
        LocalDateTime now = LocalDateTime.now();
        VotingSession session = new VotingSession(
            UUID.randomUUID(),
            UUID.randomUUID(),
            now.minusMinutes(5),
            now
        );

        assertFalse(session.isOpen(now));
    }

    @Test
    @DisplayName("Deve retornar false quando horário atual for após o horário de fechamento")
    void shouldReturnFalseWhenCurrentTimeIsAfterClosingTime() {
        LocalDateTime now = LocalDateTime.now();
        VotingSession session = new VotingSession(
            UUID.randomUUID(),
            UUID.randomUUID(),
            now.minusMinutes(10),
            now.minusMinutes(1)
        );

        assertFalse(session.isOpen(now));
    }
}

