package br.com.bank.voting.application.service;

import br.com.bank.voting.application.dto.result.VotingResultResult;
import br.com.bank.voting.application.port.out.AgendaRepositoryPort;
import br.com.bank.voting.application.port.out.SessionRepositoryPort;
import br.com.bank.voting.application.port.out.VoteRepositoryPort;
import br.com.bank.voting.domain.model.Agenda;
import br.com.bank.voting.domain.model.Vote;
import br.com.bank.voting.domain.model.VotingSession;
import br.com.bank.voting.domain.model.enums.SessionStatus;
import br.com.bank.voting.domain.model.enums.VoteChoice;
import br.com.bank.voting.domain.model.enums.VotingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetResultService Tests")
class GetResultServiceTest {

    @Mock
    private AgendaRepositoryPort agendaRepository;

    @Mock
    private SessionRepositoryPort sessionRepository;

    @Mock
    private VoteRepositoryPort voteRepository;

    @InjectMocks
    private GetResultService getResultService;

    private UUID agendaId;
    private Agenda agenda;
    private VotingSession openSession;
    private VotingSession closedSession;

    @BeforeEach
    void setUp() {
        agendaId = UUID.randomUUID();
        agenda = new Agenda(agendaId, "Pauta Teste", LocalDateTime.now());
        
        LocalDateTime now = LocalDateTime.now();
        openSession = new VotingSession(
            UUID.randomUUID(), agendaId, now, now.plusMinutes(10)
        );
        
        closedSession = new VotingSession(
            UUID.randomUUID(), agendaId, now.minusMinutes(10), now.minusMinutes(5)
        );
    }

    @Test
    @DisplayName("Deve retornar resultado com sessão aberta e votos aprovados")
    void shouldReturnResultWithOpenSessionAndApprovedVotes() {
        List<Vote> votes = Arrays.asList(
            new Vote(UUID.randomUUID(), agendaId, "11111111111", VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "22222222222", VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "33333333333", VoteChoice.NO, LocalDateTime.now())
        );

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(openSession));
        when(voteRepository.findAllByAgendaId(agendaId)).thenReturn(votes);

        VotingResultResult result = getResultService.getResult(agendaId);

        assertNotNull(result);
        assertEquals(agendaId, result.agendaId());
        assertEquals(SessionStatus.OPEN, result.sessionStatus());
        assertEquals(2L, result.yes());
        assertEquals(1L, result.no());
        assertEquals(3L, result.total());
        assertEquals(VotingResult.APPROVED, result.result());
    }

    @Test
    @DisplayName("Deve retornar resultado com sessão fechada e votos rejeitados")
    void shouldReturnResultWithClosedSessionAndRejectedVotes() {
        List<Vote> votes = Arrays.asList(
            new Vote(UUID.randomUUID(), agendaId, "11111111111", VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "22222222222", VoteChoice.NO, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "33333333333", VoteChoice.NO, LocalDateTime.now())
        );

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(closedSession));
        when(voteRepository.findAllByAgendaId(agendaId)).thenReturn(votes);

        VotingResultResult result = getResultService.getResult(agendaId);

        assertNotNull(result);
        assertEquals(SessionStatus.CLOSED, result.sessionStatus());
        assertEquals(1L, result.yes());
        assertEquals(2L, result.no());
        assertEquals(3L, result.total());
        assertEquals(VotingResult.REJECTED, result.result());
    }

    @Test
    @DisplayName("Deve retornar resultado rejeitado quando houver empate")
    void shouldReturnRejectedResultWhenThereIsATie() {
        List<Vote> votes = Arrays.asList(
            new Vote(UUID.randomUUID(), agendaId, "11111111111", VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "22222222222", VoteChoice.NO, LocalDateTime.now())
        );

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(openSession));
        when(voteRepository.findAllByAgendaId(agendaId)).thenReturn(votes);

        VotingResultResult result = getResultService.getResult(agendaId);

        assertEquals(1L, result.yes());
        assertEquals(1L, result.no());
        assertEquals(VotingResult.REJECTED, result.result());
    }

    @Test
    @DisplayName("Deve retornar resultado com zero votos quando não houver votos")
    void shouldReturnResultWithZeroVotesWhenNoVotesExist() {
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(openSession));
        when(voteRepository.findAllByAgendaId(agendaId)).thenReturn(Collections.emptyList());

        VotingResultResult result = getResultService.getResult(agendaId);

        assertEquals(0L, result.yes());
        assertEquals(0L, result.no());
        assertEquals(0L, result.total());
        assertEquals(VotingResult.REJECTED, result.result());
    }

    @Test
    @DisplayName("Deve retornar sessão fechada quando não existir sessão")
    void shouldReturnClosedSessionWhenNoSessionExists() {
        List<Vote> votes = Collections.emptyList();

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.empty());
        when(voteRepository.findAllByAgendaId(agendaId)).thenReturn(votes);

        VotingResultResult result = getResultService.getResult(agendaId);

        assertEquals(SessionStatus.CLOSED, result.sessionStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção quando pauta não existir")
    void shouldThrowExceptionWhenAgendaNotFound() {
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> getResultService.getResult(agendaId));

        assertEquals("Agenda not found: " + agendaId, exception.getMessage());
    }

    @Test
    @DisplayName("Deve contabilizar corretamente múltiplos votos SIM")
    void shouldCountMultipleYesVotesCorrectly() {
        List<Vote> votes = Arrays.asList(
            new Vote(UUID.randomUUID(), agendaId, "11111111111", VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "22222222222", VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "33333333333", VoteChoice.YES, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "44444444444", VoteChoice.YES, LocalDateTime.now())
        );

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(openSession));
        when(voteRepository.findAllByAgendaId(agendaId)).thenReturn(votes);

        VotingResultResult result = getResultService.getResult(agendaId);

        assertEquals(4L, result.yes());
        assertEquals(0L, result.no());
        assertEquals(4L, result.total());
    }

    @Test
    @DisplayName("Deve contabilizar corretamente múltiplos votos NÃO")
    void shouldCountMultipleNoVotesCorrectly() {
        List<Vote> votes = Arrays.asList(
            new Vote(UUID.randomUUID(), agendaId, "11111111111", VoteChoice.NO, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "22222222222", VoteChoice.NO, LocalDateTime.now()),
            new Vote(UUID.randomUUID(), agendaId, "33333333333", VoteChoice.NO, LocalDateTime.now())
        );

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(openSession));
        when(voteRepository.findAllByAgendaId(agendaId)).thenReturn(votes);

        VotingResultResult result = getResultService.getResult(agendaId);

        assertEquals(0L, result.yes());
        assertEquals(3L, result.no());
        assertEquals(3L, result.total());
    }
}

