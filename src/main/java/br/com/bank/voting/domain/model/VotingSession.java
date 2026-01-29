package br.com.bank.voting.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class VotingSession {
    private UUID id;
    private UUID agendaId;
    private LocalDateTime openedAt;
    private LocalDateTime closesAt;

    public VotingSession(UUID id, UUID agendaId, LocalDateTime openedAt, LocalDateTime closesAt) {
        this.id = id;
        this.agendaId = agendaId;
        this.openedAt = openedAt;
        this.closesAt = closesAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAgendaId() {
        return agendaId;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public LocalDateTime getClosesAt() {
        return closesAt;
    }

    public boolean isOpen(LocalDateTime now) {
        return now.isBefore(closesAt);
    }
}

