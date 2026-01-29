package br.com.bank.voting.domain.rules;

import br.com.bank.voting.domain.model.VotingSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SessionRules Tests")
class SessionRulesTest {

    @Test
    @DisplayName("Deve retornar true quando sessão estiver aberta")
    void shouldReturnTrueWhenSessionIsOpen() {
        LocalDateTime now = LocalDateTime.now();
        VotingSession session = new VotingSession(
            UUID.randomUUID(),
            UUID.randomUUID(),
            now,
            now.plusMinutes(5)
        );

        assertTrue(SessionRules.isSessionOpen(session, now));
    }

    @Test
    @DisplayName("Deve retornar false quando sessão estiver fechada")
    void shouldReturnFalseWhenSessionIsClosed() {
        LocalDateTime now = LocalDateTime.now();
        VotingSession session = new VotingSession(
            UUID.randomUUID(),
            UUID.randomUUID(),
            now.minusMinutes(10),
            now.minusMinutes(5)
        );

        assertFalse(SessionRules.isSessionOpen(session, now));
    }

    @Test
    @DisplayName("Deve retornar false quando sessão for null")
    void shouldReturnFalseWhenSessionIsNull() {
        assertFalse(SessionRules.isSessionOpen(null, LocalDateTime.now()));
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

        assertFalse(SessionRules.isSessionOpen(session, now));
    }

    @Test
    @DisplayName("Deve calcular corretamente o horário de fechamento com 1 minuto")
    void shouldCalculateClosingTimeWithOneMinute() {
        LocalDateTime openedAt = LocalDateTime.of(2026, 1, 29, 10, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2026, 1, 29, 10, 1, 0);

        LocalDateTime result = SessionRules.calculateClosingTime(openedAt, 1);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Deve calcular corretamente o horário de fechamento com múltiplos minutos")
    void shouldCalculateClosingTimeWithMultipleMinutes() {
        LocalDateTime openedAt = LocalDateTime.of(2026, 1, 29, 10, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2026, 1, 29, 10, 15, 0);

        LocalDateTime result = SessionRules.calculateClosingTime(openedAt, 15);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Deve calcular corretamente o horário de fechamento com 60 minutos")
    void shouldCalculateClosingTimeWithSixtyMinutes() {
        LocalDateTime openedAt = LocalDateTime.of(2026, 1, 29, 10, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2026, 1, 29, 11, 0, 0);

        LocalDateTime result = SessionRules.calculateClosingTime(openedAt, 60);

        assertEquals(expected, result);
    }
}

