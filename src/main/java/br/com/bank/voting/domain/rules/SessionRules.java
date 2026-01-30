package br.com.bank.voting.domain.rules;

import br.com.bank.voting.domain.model.VotingSession;

import java.time.LocalDateTime;

/**
 * Regras de negócio relacionadas a sessões de votação.
 * Contém lógica pura de domínio, sem dependências de frameworks.
 */
public class SessionRules {

    /**
     * Verifica se uma sessão de votação está aberta no momento atual.
     *
     * @param session sessão de votação a ser verificada
     * @param now data/hora atual para comparação
     * @return true se a sessão estiver aberta, false caso contrário ou se a sessão for null
     */
    public static boolean isSessionOpen(VotingSession session, LocalDateTime now) {
        if (session == null) {
            return false;
        }
        return session.isOpen(now);
    }

    /**
     * Calcula o horário de fechamento de uma sessão baseado no horário de abertura e duração.
     *
     * @param openedAt data/hora de abertura da sessão
     * @param durationMinutes duração da sessão em minutos
     * @return data/hora de fechamento da sessão
     */
    public static LocalDateTime calculateClosingTime(LocalDateTime openedAt, int durationMinutes) {
        return openedAt.plusMinutes(durationMinutes);
    }
}

