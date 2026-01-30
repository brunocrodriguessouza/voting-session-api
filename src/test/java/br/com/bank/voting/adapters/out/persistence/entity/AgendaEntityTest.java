package br.com.bank.voting.adapters.out.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AgendaEntity Tests")
class AgendaEntityTest {

    @Test
    @DisplayName("Deve criar entidade com construtor padrão")
    void shouldCreateEntityWithDefaultConstructor() {
        AgendaEntity entity = new AgendaEntity();

        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getTitle());
        assertNull(entity.getCreatedAt());
    }

    @Test
    @DisplayName("Deve criar entidade com construtor parametrizado")
    void shouldCreateEntityWithParameterizedConstructor() {
        UUID id = UUID.randomUUID();
        String title = "Pauta Teste";
        LocalDateTime createdAt = LocalDateTime.now();

        AgendaEntity entity = new AgendaEntity(id, title, createdAt);

        assertEquals(id, entity.getId());
        assertEquals(title, entity.getTitle());
        assertEquals(createdAt, entity.getCreatedAt());
    }

    @Test
    @DisplayName("Deve definir e obter valores usando setters e getters")
    void shouldSetAndGetValuesUsingSettersAndGetters() {
        AgendaEntity entity = new AgendaEntity();
        UUID id = UUID.randomUUID();
        String title = "Nova Pauta";
        LocalDateTime createdAt = LocalDateTime.now();

        entity.setId(id);
        entity.setTitle(title);
        entity.setCreatedAt(createdAt);

        assertEquals(id, entity.getId());
        assertEquals(title, entity.getTitle());
        assertEquals(createdAt, entity.getCreatedAt());
    }
}

