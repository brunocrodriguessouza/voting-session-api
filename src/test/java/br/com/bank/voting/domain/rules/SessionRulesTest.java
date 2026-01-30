package br.com.bank.voting.domain.rules;

import br.com.bank.voting.domain.model.VotingSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @ParameterizedTest
    @CsvSource({
        "2026-01-29T10:00:00, 1, 2026-01-29T10:01:00",
        "2026-01-29T10:00:00, 15, 2026-01-29T10:15:00",
        "2026-01-29T10:00:00, 60, 2026-01-29T11:00:00"
    })
    @DisplayName("Deve calcular corretamente o horário de fechamento")
    void shouldCalculateClosingTimeCorrectly(String openedAtStr, int durationMinutes, String expectedStr) {
        LocalDateTime openedAt = LocalDateTime.parse(openedAtStr);
        LocalDateTime expected = LocalDateTime.parse(expectedStr);

        LocalDateTime result = SessionRules.calculateClosingTime(openedAt, durationMinutes);

        assertEquals(expected, result);
    }
}


