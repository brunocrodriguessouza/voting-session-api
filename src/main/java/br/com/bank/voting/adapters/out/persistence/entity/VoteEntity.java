package br.com.bank.voting.adapters.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"agenda_id", "cpf"})
})
public class VoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "agenda_id")
    private UUID agendaId;

    @Column(nullable = false, length = 11)
    private String cpf;

    @Column(nullable = false, length = 3)
    private String choice;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public VoteEntity() {
    }

    public VoteEntity(UUID id, UUID agendaId, String cpf, String choice, LocalDateTime createdAt) {
        this.id = id;
        this.agendaId = agendaId;
        this.cpf = cpf;
        this.choice = choice;
        this.createdAt = createdAt;
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}


