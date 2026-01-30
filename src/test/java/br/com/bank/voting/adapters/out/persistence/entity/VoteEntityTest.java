package br.com.bank.voting.adapters.out.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VoteEntity Tests")
class VoteEntityTest {

    @Test
    @DisplayName("Deve criar entidade com construtor padrão")
    void shouldCreateEntityWithDefaultConstructor() {
        VoteEntity entity = new VoteEntity();

        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getAgendaId());
        assertNull(entity.getCpf());
        assertNull(entity.getChoice());
        assertNull(entity.getCreatedAt());
    }

    @Test
    @DisplayName("Deve criar entidade com construtor parametrizado")
    void shouldCreateEntityWithParameterizedConstructor() {
        UUID id = UUID.randomUUID();
        UUID agendaId = UUID.randomUUID();
        String cpf = "12345678901";
        String choice = "YES";
        LocalDateTime createdAt = LocalDateTime.now();

        VoteEntity entity = new VoteEntity(id, agendaId, cpf, choice, createdAt);

        assertEquals(id, entity.getId());
        assertEquals(agendaId, entity.getAgendaId());
        assertEquals(cpf, entity.getCpf());
        assertEquals(choice, entity.getChoice());
        assertEquals(createdAt, entity.getCreatedAt());
    }

    @Test
    @DisplayName("Deve definir e obter valores usando setters e getters")
    void shouldSetAndGetValuesUsingSettersAndGetters() {
        VoteEntity entity = new VoteEntity();
        UUID id = UUID.randomUUID();
        UUID agendaId = UUID.randomUUID();
        String cpf = "98765432100";
        String choice = "NO";
        LocalDateTime createdAt = LocalDateTime.now();

        entity.setId(id);
        entity.setAgendaId(agendaId);
        entity.setCpf(cpf);
        entity.setChoice(choice);
        entity.setCreatedAt(createdAt);

        assertEquals(id, entity.getId());
        assertEquals(agendaId, entity.getAgendaId());
        assertEquals(cpf, entity.getCpf());
        assertEquals(choice, entity.getChoice());
        assertEquals(createdAt, entity.getCreatedAt());
    }
}

