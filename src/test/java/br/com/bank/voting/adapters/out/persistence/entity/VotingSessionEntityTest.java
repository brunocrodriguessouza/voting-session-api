package br.com.bank.voting.adapters.out.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VotingSessionEntity Tests")
class VotingSessionEntityTest {

    @Test
    @DisplayName("Deve criar entidade com construtor padrão")
    void shouldCreateEntityWithDefaultConstructor() {
        VotingSessionEntity entity = new VotingSessionEntity();

        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getAgendaId());
        assertNull(entity.getOpenedAt());
        assertNull(entity.getClosesAt());
    }

    @Test
    @DisplayName("Deve criar entidade com construtor parametrizado")
    void shouldCreateEntityWithParameterizedConstructor() {
        UUID id = UUID.randomUUID();
        UUID agendaId = UUID.randomUUID();
        LocalDateTime openedAt = LocalDateTime.now();
        LocalDateTime closesAt = openedAt.plusMinutes(10);

        VotingSessionEntity entity = new VotingSessionEntity(id, agendaId, openedAt, closesAt);

        assertEquals(id, entity.getId());
        assertEquals(agendaId, entity.getAgendaId());
        assertEquals(openedAt, entity.getOpenedAt());
        assertEquals(closesAt, entity.getClosesAt());
    }

    @Test
    @DisplayName("Deve definir e obter valores usando setters e getters")
    void shouldSetAndGetValuesUsingSettersAndGetters() {
        VotingSessionEntity entity = new VotingSessionEntity();
        UUID id = UUID.randomUUID();
        UUID agendaId = UUID.randomUUID();
        LocalDateTime openedAt = LocalDateTime.now();
        LocalDateTime closesAt = openedAt.plusMinutes(5);

        entity.setId(id);
        entity.setAgendaId(agendaId);
        entity.setOpenedAt(openedAt);
        entity.setClosesAt(closesAt);

        assertEquals(id, entity.getId());
        assertEquals(agendaId, entity.getAgendaId());
        assertEquals(openedAt, entity.getOpenedAt());
        assertEquals(closesAt, entity.getClosesAt());
    }
}

