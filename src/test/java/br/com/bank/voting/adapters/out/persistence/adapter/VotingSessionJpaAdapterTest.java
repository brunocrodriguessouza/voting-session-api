package br.com.bank.voting.adapters.out.persistence.adapter;

import br.com.bank.voting.adapters.out.persistence.entity.VotingSessionEntity;
import br.com.bank.voting.adapters.out.persistence.repository.VotingSessionJpaRepository;
import br.com.bank.voting.domain.model.VotingSession;
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
@DisplayName("VotingSessionJpaAdapter Tests")
class VotingSessionJpaAdapterTest {

    @Mock
    private VotingSessionJpaRepository repository;

    @InjectMocks
    private VotingSessionJpaAdapter adapter;

    private UUID sessionId;
    private UUID agendaId;
    private VotingSession domainSession;
    private VotingSessionEntity entity;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();
        agendaId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime closesAt = now.plusMinutes(1);
        
        domainSession = new VotingSession(null, agendaId, now, closesAt);
        entity = new VotingSessionEntity(sessionId, agendaId, now, closesAt);
    }

    @Test
    @DisplayName("Deve salvar sessão e retornar domain model")
    void shouldSaveSessionAndReturnDomainModel() {
        VotingSessionEntity savedEntity = new VotingSessionEntity(sessionId, agendaId, 
                domainSession.getOpenedAt(), domainSession.getClosesAt());
        
        when(repository.save(any(VotingSessionEntity.class))).thenReturn(savedEntity);

        VotingSession saved = adapter.save(domainSession);

        assertNotNull(saved);
        assertEquals(sessionId, saved.getId());
        assertEquals(agendaId, saved.getAgendaId());
        assertEquals(domainSession.getOpenedAt(), saved.getOpenedAt());
        assertEquals(domainSession.getClosesAt(), saved.getClosesAt());
        verify(repository, times(1)).save(any(VotingSessionEntity.class));
    }

    @Test
    @DisplayName("Deve salvar sessão sem ID e permitir JPA gerar")
    void shouldSaveSessionWithoutId() {
        VotingSession sessionWithoutId = new VotingSession(null, agendaId, 
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(1));
        VotingSessionEntity savedEntity = new VotingSessionEntity(sessionId, agendaId, 
                sessionWithoutId.getOpenedAt(), sessionWithoutId.getClosesAt());
        
        when(repository.save(any(VotingSessionEntity.class))).thenReturn(savedEntity);

        VotingSession saved = adapter.save(sessionWithoutId);

        assertNotNull(saved.getId());
        verify(repository).save(argThat(entity -> entity.getId() == null));
    }

    @Test
    @DisplayName("Deve encontrar sessão por agendaId")
    void shouldFindSessionByAgendaId() {
        when(repository.findByAgendaId(agendaId)).thenReturn(Optional.of(entity));

        Optional<VotingSession> found = adapter.findByAgendaId(agendaId);

        assertTrue(found.isPresent());
        assertEquals(sessionId, found.get().getId());
        assertEquals(agendaId, found.get().getAgendaId());
        verify(repository, times(1)).findByAgendaId(agendaId);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando sessão não existir")
    void shouldReturnEmptyOptionalWhenSessionNotFound() {
        when(repository.findByAgendaId(agendaId)).thenReturn(Optional.empty());

        Optional<VotingSession> found = adapter.findByAgendaId(agendaId);

        assertTrue(found.isEmpty());
        verify(repository, times(1)).findByAgendaId(agendaId);
    }

    @Test
    @DisplayName("Deve converter corretamente de Entity para Domain")
    void shouldConvertEntityToDomainCorrectly() {
        when(repository.findByAgendaId(agendaId)).thenReturn(Optional.of(entity));

        Optional<VotingSession> found = adapter.findByAgendaId(agendaId);

        assertTrue(found.isPresent());
        VotingSession session = found.get();
        assertEquals(entity.getId(), session.getId());
        assertEquals(entity.getAgendaId(), session.getAgendaId());
        assertEquals(entity.getOpenedAt(), session.getOpenedAt());
        assertEquals(entity.getClosesAt(), session.getClosesAt());
    }

    @Test
    @DisplayName("Deve converter corretamente de Domain para Entity")
    void shouldConvertDomainToEntityCorrectly() {
        VotingSessionEntity savedEntity = new VotingSessionEntity(sessionId, 
                domainSession.getAgendaId(), domainSession.getOpenedAt(), domainSession.getClosesAt());
        when(repository.save(any(VotingSessionEntity.class))).thenReturn(savedEntity);

        adapter.save(domainSession);

        verify(repository).save(argThat(entity -> 
            entity.getAgendaId().equals(domainSession.getAgendaId()) &&
            entity.getOpenedAt().equals(domainSession.getOpenedAt()) &&
            entity.getClosesAt().equals(domainSession.getClosesAt())
        ));
    }
}

