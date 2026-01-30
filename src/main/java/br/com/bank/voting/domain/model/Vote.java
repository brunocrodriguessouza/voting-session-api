package br.com.bank.voting.domain.model;

import br.com.bank.voting.domain.model.enums.VoteChoice;

import java.time.LocalDateTime;
import java.util.UUID;

public class Vote {
    private UUID id;
    private UUID agendaId;
    private String cpf;
    private VoteChoice choice;
    private LocalDateTime createdAt;

    public Vote(UUID id, UUID agendaId, String cpf, VoteChoice choice, LocalDateTime createdAt) {
        this.id = id;
        this.agendaId = agendaId;
        this.cpf = cpf;
        this.choice = choice;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAgendaId() {
        return agendaId;
    }

    public String getCpf() {
        return cpf;
    }

    public VoteChoice getChoice() {
        return choice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}


