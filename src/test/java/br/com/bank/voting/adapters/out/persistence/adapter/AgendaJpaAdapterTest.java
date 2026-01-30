package br.com.bank.voting.adapters.out.persistence.adapter;

import br.com.bank.voting.adapters.out.persistence.entity.AgendaEntity;
import br.com.bank.voting.adapters.out.persistence.repository.AgendaJpaRepository;
import br.com.bank.voting.domain.model.Agenda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AgendaJpaAdapter Tests")
class AgendaJpaAdapterTest {

    @Mock
    private AgendaJpaRepository repository;

    @InjectMocks
    private AgendaJpaAdapter adapter;

    private UUID agendaId;
    private Agenda domainAgenda;
    private AgendaEntity entity;

    @BeforeEach
    void setUp() {
        agendaId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        
        domainAgenda = new Agenda(null, "Pauta Teste", now);
        entity = new AgendaEntity(agendaId, "Pauta Teste", now);
    }

    @Test
    @DisplayName("Deve salvar agenda e retornar domain model")
    void shouldSaveAgendaAndReturnDomainModel() {
        AgendaEntity savedEntity = new AgendaEntity(agendaId, "Pauta Teste", domainAgenda.getCreatedAt());
        
        when(repository.save(any(AgendaEntity.class))).thenReturn(savedEntity);

        Agenda saved = adapter.save(domainAgenda);

        assertNotNull(saved);
        assertEquals(agendaId, saved.getId());
        assertEquals("Pauta Teste", saved.getTitle());
        assertEquals(domainAgenda.getCreatedAt(), saved.getCreatedAt());
        verify(repository, times(1)).save(any(AgendaEntity.class));
    }

    @Test
    @DisplayName("Deve salvar agenda sem ID e permitir JPA gerar")
    void shouldSaveAgendaWithoutId() {
        Agenda agendaWithoutId = new Agenda(null, "Nova Pauta", LocalDateTime.now());
        AgendaEntity expectedEntity = new AgendaEntity(agendaId, "Nova Pauta", agendaWithoutId.getCreatedAt());
        
        when(repository.save(any(AgendaEntity.class))).thenReturn(expectedEntity);

        Agenda saved = adapter.save(agendaWithoutId);

        assertNotNull(saved.getId());
        verify(repository).save(argThat(entityToSave -> entityToSave.getId() == null));
    }

    @Test
    @DisplayName("Deve encontrar agenda por ID")
    void shouldFindAgendaById() {
        when(repository.findById(agendaId)).thenReturn(Optional.of(entity));

        Optional<Agenda> found = adapter.findById(agendaId);

        assertTrue(found.isPresent());
        assertEquals(agendaId, found.get().getId());
        assertEquals("Pauta Teste", found.get().getTitle());
        verify(repository, times(1)).findById(agendaId);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando agenda não existir")
    void shouldReturnEmptyOptionalWhenAgendaNotFound() {
        when(repository.findById(agendaId)).thenReturn(Optional.empty());

        Optional<Agenda> found = adapter.findById(agendaId);

        assertTrue(found.isEmpty());
        verify(repository, times(1)).findById(agendaId);
    }

    @Test
    @DisplayName("Deve converter corretamente de Entity para Domain")
    void shouldConvertEntityToDomainCorrectly() {
        when(repository.findById(agendaId)).thenReturn(Optional.of(entity));

        Optional<Agenda> found = adapter.findById(agendaId);

        assertTrue(found.isPresent());
        Agenda agenda = found.get();
        assertEquals(entity.getId(), agenda.getId());
        assertEquals(entity.getTitle(), agenda.getTitle());
        assertEquals(entity.getCreatedAt(), agenda.getCreatedAt());
    }

    @Test
    @DisplayName("Deve converter corretamente de Domain para Entity")
    void shouldConvertDomainToEntityCorrectly() {
        AgendaEntity expectedEntity = new AgendaEntity(agendaId, domainAgenda.getTitle(), domainAgenda.getCreatedAt());
        when(repository.save(any(AgendaEntity.class))).thenReturn(expectedEntity);

        adapter.save(domainAgenda);

        verify(repository).save(argThat(entityToSave -> 
            entityToSave.getTitle().equals(domainAgenda.getTitle()) &&
            entityToSave.getCreatedAt().equals(domainAgenda.getCreatedAt())
        ));
    }

    @Test
    @DisplayName("Deve salvar agenda com ID existente")
    void shouldSaveAgendaWithExistingId() {
        Agenda agendaWithId = new Agenda(agendaId, "Pauta com ID", LocalDateTime.now());
        AgendaEntity savedEntity = new AgendaEntity(agendaId, "Pauta com ID", agendaWithId.getCreatedAt());
        
        when(repository.save(any(AgendaEntity.class))).thenReturn(savedEntity);

        Agenda saved = adapter.save(agendaWithId);

        assertNotNull(saved);
        assertEquals(agendaId, saved.getId());
        verify(repository).save(argThat(entityToSave -> 
            entityToSave.getId() != null &&
            entityToSave.getId().equals(agendaId)
        ));
    }
}


