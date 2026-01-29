package br.com.bank.voting.adapters.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "voting_sessions")
public class VotingSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID agendaId;

    @Column(nullable = false)
    private LocalDateTime openedAt;

    @Column(nullable = false)
    private LocalDateTime closesAt;

    public VotingSessionEntity() {
    }

    public VotingSessionEntity(UUID id, UUID agendaId, LocalDateTime openedAt, LocalDateTime closesAt) {
        this.id = id;
        this.agendaId = agendaId;
        this.openedAt = openedAt;
        this.closesAt = closesAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAgendaId() {
        return agendaId;
    }

    public void setAgendaId(UUID agendaId) {
        this.agendaId = agendaId;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public LocalDateTime getClosesAt() {
        return closesAt;
    }

    public void setClosesAt(LocalDateTime closesAt) {
        this.closesAt = closesAt;
    }
}

