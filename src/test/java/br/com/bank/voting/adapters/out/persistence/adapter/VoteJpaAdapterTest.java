package br.com.bank.voting.adapters.out.persistence.adapter;

import br.com.bank.voting.adapters.out.persistence.entity.VoteEntity;
import br.com.bank.voting.adapters.out.persistence.repository.VoteJpaRepository;
import br.com.bank.voting.domain.model.Vote;
import br.com.bank.voting.domain.model.enums.VoteChoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VoteJpaAdapter Tests")
class VoteJpaAdapterTest {

    @Mock
    private VoteJpaRepository repository;

    @InjectMocks
    private VoteJpaAdapter adapter;

    private UUID voteId;
    private UUID agendaId;
    private String cpf;
    private Vote domainVote;
    private VoteEntity entity;

    @BeforeEach
    void setUp() {
        voteId = UUID.randomUUID();
        agendaId = UUID.randomUUID();
        cpf = "12345678901";
        LocalDateTime now = LocalDateTime.now();
        
        domainVote = new Vote(null, agendaId, cpf, VoteChoice.YES, now);
        entity = new VoteEntity();
        entity.setId(voteId);
        entity.setAgendaId(agendaId);
        entity.setCpf(cpf);
        entity.setChoice("YES");
        entity.setCreatedAt(now);
    }

    @Test
    @DisplayName("Deve salvar voto e retornar domain model")
    void shouldSaveVoteAndReturnDomainModel() {
        VoteEntity savedEntity = new VoteEntity();
        savedEntity.setId(voteId);
        savedEntity.setAgendaId(agendaId);
        savedEntity.setCpf(cpf);
        savedEntity.setChoice("YES");
        savedEntity.setCreatedAt(domainVote.getCreatedAt());
        
        when(repository.save(any(VoteEntity.class))).thenReturn(savedEntity);

        Vote saved = adapter.save(domainVote);

        assertNotNull(saved);
        assertEquals(voteId, saved.getId());
        assertEquals(agendaId, saved.getAgendaId());
        assertEquals(cpf, saved.getCpf());
        assertEquals(VoteChoice.YES, saved.getChoice());
        verify(repository, times(1)).save(any(VoteEntity.class));
    }

    @Test
    @DisplayName("Deve salvar voto sem ID e permitir JPA gerar")
    void shouldSaveVoteWithoutId() {
        Vote voteWithoutId = new Vote(null, agendaId, cpf, VoteChoice.NO, LocalDateTime.now());
        VoteEntity expectedEntity = new VoteEntity();
        expectedEntity.setId(voteId);
        expectedEntity.setAgendaId(agendaId);
        expectedEntity.setCpf(cpf);
        expectedEntity.setChoice("NO");
        expectedEntity.setCreatedAt(voteWithoutId.getCreatedAt());
        
        when(repository.save(any(VoteEntity.class))).thenReturn(expectedEntity);

        Vote saved = adapter.save(voteWithoutId);

        assertNotNull(saved.getId());
        verify(repository).save(argThat(entityToSave -> entityToSave.getId() == null));
    }

    @Test
    @DisplayName("Deve encontrar voto por agendaId e CPF")
    void shouldFindVoteByAgendaIdAndCpf() {
        when(repository.findByAgendaIdAndCpf(agendaId, cpf)).thenReturn(Optional.of(entity));

        Optional<Vote> found = adapter.findByAgendaIdAndCpf(agendaId, cpf);

        assertTrue(found.isPresent());
        assertEquals(voteId, found.get().getId());
        assertEquals(agendaId, found.get().getAgendaId());
        assertEquals(cpf, found.get().getCpf());
        assertEquals(VoteChoice.YES, found.get().getChoice());
        verify(repository, times(1)).findByAgendaIdAndCpf(agendaId, cpf);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando voto não existir")
    void shouldReturnEmptyOptionalWhenVoteNotFound() {
        when(repository.findByAgendaIdAndCpf(agendaId, cpf)).thenReturn(Optional.empty());

        Optional<Vote> found = adapter.findByAgendaIdAndCpf(agendaId, cpf);

        assertTrue(found.isEmpty());
        verify(repository, times(1)).findByAgendaIdAndCpf(agendaId, cpf);
    }

    @Test
    @DisplayName("Deve encontrar todos os votos por agendaId")
    void shouldFindAllVotesByAgendaId() {
        VoteEntity entity1 = new VoteEntity();
        entity1.setId(UUID.randomUUID());
        entity1.setAgendaId(agendaId);
        entity1.setCpf("11111111111");
        entity1.setChoice("YES");
        entity1.setCreatedAt(LocalDateTime.now());

        VoteEntity entity2 = new VoteEntity();
        entity2.setId(UUID.randomUUID());
        entity2.setAgendaId(agendaId);
        entity2.setCpf("22222222222");
        entity2.setChoice("NO");
        entity2.setCreatedAt(LocalDateTime.now());

        when(repository.findAllByAgendaId(agendaId)).thenReturn(Arrays.asList(entity1, entity2));

        List<Vote> votes = adapter.findAllByAgendaId(agendaId);

        assertEquals(2, votes.size());
        assertEquals(agendaId, votes.get(0).getAgendaId());
        assertEquals(agendaId, votes.get(1).getAgendaId());
        verify(repository, times(1)).findAllByAgendaId(agendaId);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver votos")
    void shouldReturnEmptyListWhenNoVotesFound() {
        when(repository.findAllByAgendaId(agendaId)).thenReturn(List.of());

        List<Vote> votes = adapter.findAllByAgendaId(agendaId);

        assertTrue(votes.isEmpty());
        verify(repository, times(1)).findAllByAgendaId(agendaId);
    }

    @Test
    @DisplayName("Deve converter corretamente de Entity para Domain")
    void shouldConvertEntityToDomainCorrectly() {
        when(repository.findByAgendaIdAndCpf(agendaId, cpf)).thenReturn(Optional.of(entity));

        Optional<Vote> found = adapter.findByAgendaIdAndCpf(agendaId, cpf);

        assertTrue(found.isPresent());
        Vote vote = found.get();
        assertEquals(entity.getId(), vote.getId());
        assertEquals(entity.getAgendaId(), vote.getAgendaId());
        assertEquals(entity.getCpf(), vote.getCpf());
        assertEquals(VoteChoice.valueOf(entity.getChoice()), vote.getChoice());
        assertEquals(entity.getCreatedAt(), vote.getCreatedAt());
    }

    @Test
    @DisplayName("Deve converter corretamente de Domain para Entity")
    void shouldConvertDomainToEntityCorrectly() {
        VoteEntity expectedEntity = new VoteEntity();
        expectedEntity.setId(voteId);
        expectedEntity.setAgendaId(domainVote.getAgendaId());
        expectedEntity.setCpf(domainVote.getCpf());
        expectedEntity.setChoice(domainVote.getChoice().name());
        expectedEntity.setCreatedAt(domainVote.getCreatedAt());
        
        when(repository.save(any(VoteEntity.class))).thenReturn(expectedEntity);

        adapter.save(domainVote);

        verify(repository).save(argThat(entityToSave -> 
            entityToSave.getAgendaId().equals(domainVote.getAgendaId()) &&
            entityToSave.getCpf().equals(domainVote.getCpf()) &&
            entityToSave.getChoice().equals(domainVote.getChoice().name()) &&
            entityToSave.getCreatedAt().equals(domainVote.getCreatedAt())
        ));
    }

    @Test
    @DisplayName("Deve converter corretamente VoteChoice NO")
    void shouldConvertNoVoteChoiceCorrectly() {
        Vote noVote = new Vote(null, agendaId, cpf, VoteChoice.NO, LocalDateTime.now());
        VoteEntity savedEntity = new VoteEntity();
        savedEntity.setId(voteId);
        savedEntity.setAgendaId(agendaId);
        savedEntity.setCpf(cpf);
        savedEntity.setChoice("NO");
        savedEntity.setCreatedAt(noVote.getCreatedAt());
        
        when(repository.save(any(VoteEntity.class))).thenReturn(savedEntity);

        Vote saved = adapter.save(noVote);

        assertEquals(VoteChoice.NO, saved.getChoice());
    }

    @Test
    @DisplayName("Deve salvar voto com ID existente")
    void shouldSaveVoteWithExistingId() {
        Vote voteWithId = new Vote(voteId, agendaId, cpf, VoteChoice.YES, LocalDateTime.now());
        VoteEntity savedEntity = new VoteEntity();
        savedEntity.setId(voteId);
        savedEntity.setAgendaId(agendaId);
        savedEntity.setCpf(cpf);
        savedEntity.setChoice("YES");
        savedEntity.setCreatedAt(voteWithId.getCreatedAt());
        
        when(repository.save(any(VoteEntity.class))).thenReturn(savedEntity);

        Vote saved = adapter.save(voteWithId);

        assertNotNull(saved);
        assertEquals(voteId, saved.getId());
        verify(repository).save(argThat(entityToSave -> 
            entityToSave.getId() != null &&
            entityToSave.getId().equals(voteId)
        ));
    }

    @Test
    @DisplayName("Deve converter lista de votos corretamente")
    void shouldConvertListOfVotesCorrectly() {
        VoteEntity entity1 = new VoteEntity();
        entity1.setId(UUID.randomUUID());
        entity1.setAgendaId(agendaId);
        entity1.setCpf("11111111111");
        entity1.setChoice("YES");
        entity1.setCreatedAt(LocalDateTime.now());

        VoteEntity entity2 = new VoteEntity();
        entity2.setId(UUID.randomUUID());
        entity2.setAgendaId(agendaId);
        entity2.setCpf("22222222222");
        entity2.setChoice("NO");
        entity2.setCreatedAt(LocalDateTime.now());

        when(repository.findAllByAgendaId(agendaId)).thenReturn(Arrays.asList(entity1, entity2));

        List<Vote> votes = adapter.findAllByAgendaId(agendaId);

        assertEquals(2, votes.size());
        assertEquals(VoteChoice.YES, votes.get(0).getChoice());
        assertEquals(VoteChoice.NO, votes.get(1).getChoice());
        assertEquals("11111111111", votes.get(0).getCpf());
        assertEquals("22222222222", votes.get(1).getCpf());
    }
}


