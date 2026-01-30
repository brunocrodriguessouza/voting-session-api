package br.com.bank.voting.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Agenda {
    private UUID id;
    private String title;
    private LocalDateTime createdAt;

    public Agenda(UUID id, String title, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}


