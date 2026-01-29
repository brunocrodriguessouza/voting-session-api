package br.com.bank.voting.domain.rules;

import br.com.bank.voting.domain.model.VotingSession;

import java.time.LocalDateTime;

/**
 * Regras de negócio relacionadas a sessões de votação.
 */
public class SessionRules {

    public static boolean isSessionOpen(VotingSession session, LocalDateTime now) {
        if (session == null) {
            return false;
        }
        return session.isOpen(now);
    }

    public static LocalDateTime calculateClosingTime(LocalDateTime openedAt, int durationMinutes) {
        return openedAt.plusMinutes(durationMinutes);
    }
}

