package br.com.bank.voting.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Agenda Tests")
class AgendaTest {

    @Test
    @DisplayName("Deve criar agenda com todos os campos")
    void shouldCreateAgendaWithAllFields() {
        UUID id = UUID.randomUUID();
        String title = "Pauta Teste";
        LocalDateTime createdAt = LocalDateTime.now();

        Agenda agenda = new Agenda(id, title, createdAt);

        assertEquals(id, agenda.getId());
        assertEquals(title, agenda.getTitle());
        assertEquals(createdAt, agenda.getCreatedAt());
    }

    @Test
    @DisplayName("Deve criar agenda com ID null")
    void shouldCreateAgendaWithNullId() {
        String title = "Nova Pauta";
        LocalDateTime createdAt = LocalDateTime.now();

        Agenda agenda = new Agenda(null, title, createdAt);

        assertNull(agenda.getId());
        assertEquals(title, agenda.getTitle());
        assertEquals(createdAt, agenda.getCreatedAt());
    }

    @Test
    @DisplayName("Deve retornar valores corretos dos getters")
    void shouldReturnCorrectValuesFromGetters() {
        UUID id = UUID.randomUUID();
        String title = "Pauta de Votação";
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 30, 10, 0, 0);

        Agenda agenda = new Agenda(id, title, createdAt);

        assertEquals(id, agenda.getId());
        assertEquals(title, agenda.getTitle());
        assertEquals(createdAt, agenda.getCreatedAt());
    }
}

