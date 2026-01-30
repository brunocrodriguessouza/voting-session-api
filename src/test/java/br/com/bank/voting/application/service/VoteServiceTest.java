package br.com.bank.voting.application.service;

import br.com.bank.voting.application.dto.command.VoteCommand;
import br.com.bank.voting.application.port.out.AgendaRepositoryPort;
import br.com.bank.voting.application.port.out.SessionRepositoryPort;
import br.com.bank.voting.application.port.out.VoteRepositoryPort;
import br.com.bank.voting.application.port.out.VoterEligibilityPort;
import br.com.bank.voting.domain.model.Agenda;
import br.com.bank.voting.domain.model.Vote;
import br.com.bank.voting.domain.model.VotingSession;
import br.com.bank.voting.domain.model.enums.VoteChoice;
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
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
@DisplayName("VoteService Tests")
class VoteServiceTest {

    @Mock
    private VoteRepositoryPort voteRepository;

    @Mock
    private SessionRepositoryPort sessionRepository;

    @Mock
    private AgendaRepositoryPort agendaRepository;

    @Mock
    private VoterEligibilityPort voterEligibilityPort;

    @InjectMocks
    private VoteService voteService;

    private UUID agendaId;
    private String cpf;
    private VoteCommand voteYesCommand;
    private VoteCommand voteNoCommand;
    private Agenda agenda;
    private VotingSession openSession;
    private VotingSession closedSession;

    @BeforeEach
    void setUp() {
        agendaId = UUID.randomUUID();
        cpf = "12345678901";
        voteYesCommand = new VoteCommand(agendaId, cpf, VoteChoice.YES);
        voteNoCommand = new VoteCommand(agendaId, cpf, VoteChoice.NO);
        
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
    @DisplayName("Deve registrar voto SIM com sucesso")
    void shouldRegisterYesVoteSuccessfully() {
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(openSession));
        when(voteRepository.findByAgendaIdAndCpf(agendaId, cpf)).thenReturn(Optional.empty());
        when(voterEligibilityPort.isEligibleToVote(cpf)).thenReturn(true);
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> voteService.vote(voteYesCommand));

        verify(voteRepository).save(argThat(vote -> 
            vote.getAgendaId().equals(agendaId) &&
            vote.getCpf().equals(cpf) &&
            vote.getChoice() == VoteChoice.YES
        ));
    }

    @Test
    @DisplayName("Deve registrar voto NÃO com sucesso")
    void shouldRegisterNoVoteSuccessfully() {
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(openSession));
        when(voteRepository.findByAgendaIdAndCpf(agendaId, cpf)).thenReturn(Optional.empty());
        when(voterEligibilityPort.isEligibleToVote(cpf)).thenReturn(true);
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> voteService.vote(voteNoCommand));

        verify(voteRepository).save(argThat(vote -> 
            vote.getAgendaId().equals(agendaId) &&
            vote.getCpf().equals(cpf) &&
            vote.getChoice() == VoteChoice.NO
        ));
    }

    @Test
    @DisplayName("Deve lançar exceção quando pauta não existir")
    void shouldThrowExceptionWhenAgendaNotFound() {
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> voteService.vote(voteYesCommand));

        assertEquals("Agenda not found: " + agendaId, exception.getMessage());
        verify(voteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando sessão não existir")
    void shouldThrowExceptionWhenSessionNotFound() {
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> voteService.vote(voteYesCommand));

        assertEquals("No session found for agenda: " + agendaId, exception.getMessage());
        verify(voteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando sessão estiver fechada")
    void shouldThrowExceptionWhenSessionIsClosed() {
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(closedSession));

        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> voteService.vote(voteYesCommand));

        assertEquals("Session is closed for agenda: " + agendaId, exception.getMessage());
        verify(voteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando associado já votou")
    void shouldThrowExceptionWhenAssociateAlreadyVoted() {
        Vote existingVote = new Vote(UUID.randomUUID(), agendaId, cpf, VoteChoice.YES, LocalDateTime.now());

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(openSession));
        when(voteRepository.findByAgendaIdAndCpf(agendaId, cpf)).thenReturn(Optional.of(existingVote));

        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> voteService.vote(voteYesCommand));

        assertEquals("Associate already voted for this agenda", exception.getMessage());
        verify(voteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando associado não for elegível para votar")
    void shouldThrowExceptionWhenAssociateIsNotEligible() {
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(openSession));
        when(voteRepository.findByAgendaIdAndCpf(agendaId, cpf)).thenReturn(Optional.empty());
        when(voterEligibilityPort.isEligibleToVote(cpf)).thenReturn(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> voteService.vote(voteYesCommand));

        assertEquals("Associate is not eligible to vote", exception.getMessage());
        verify(voteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve verificar elegibilidade antes de salvar voto")
    void shouldCheckEligibilityBeforeSavingVote() {
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(openSession));
        when(voteRepository.findByAgendaIdAndCpf(agendaId, cpf)).thenReturn(Optional.empty());
        when(voterEligibilityPort.isEligibleToVote(cpf)).thenReturn(true);
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        voteService.vote(voteYesCommand);

        verify(voterEligibilityPort).isEligibleToVote(cpf);
        verify(voteRepository).save(any(Vote.class));
    }

    @Test
    @DisplayName("Deve verificar elegibilidade na ordem correta (após validar sessão e voto duplicado)")
    void shouldCheckEligibilityInCorrectOrder() {
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(openSession));
        when(voteRepository.findByAgendaIdAndCpf(agendaId, cpf)).thenReturn(Optional.empty());
        when(voterEligibilityPort.isEligibleToVote(cpf)).thenReturn(true);
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        voteService.vote(voteYesCommand);

        // Verifica ordem: agenda -> sessão -> voto duplicado -> elegibilidade -> salvar
        var inOrder = inOrder(agendaRepository, sessionRepository, voteRepository, voterEligibilityPort);
        inOrder.verify(agendaRepository).findById(agendaId);
        inOrder.verify(sessionRepository).findByAgendaId(agendaId);
        inOrder.verify(voteRepository).findByAgendaIdAndCpf(agendaId, cpf);
        inOrder.verify(voterEligibilityPort).isEligibleToVote(cpf);
        inOrder.verify(voteRepository).save(any(Vote.class));
    }

    @Test
    @DisplayName("Não deve verificar elegibilidade se sessão estiver fechada")
    void shouldNotCheckEligibilityWhenSessionIsClosed() {
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(closedSession));

        assertThrows(IllegalStateException.class, () -> voteService.vote(voteYesCommand));

        verify(voterEligibilityPort, never()).isEligibleToVote(any());
    }

    @Test
    @DisplayName("Não deve verificar elegibilidade se associado já votou")
    void shouldNotCheckEligibilityWhenAssociateAlreadyVoted() {
        Vote existingVote = new Vote(UUID.randomUUID(), agendaId, cpf, VoteChoice.YES, LocalDateTime.now());

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(openSession));
        when(voteRepository.findByAgendaIdAndCpf(agendaId, cpf)).thenReturn(Optional.of(existingVote));

        assertThrows(IllegalStateException.class, () -> voteService.vote(voteYesCommand));

        verify(voterEligibilityPort, never()).isEligibleToVote(any());
    }

    @Test
    @DisplayName("Deve mascarar CPF corretamente quando CPF é null")
    void shouldMaskCpfCorrectlyWhenCpfIsNull() {
        VoteCommand commandWithNullCpf = new VoteCommand(agendaId, null, VoteChoice.YES);
        
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(openSession));
        when(voteRepository.findByAgendaIdAndCpf(agendaId, null)).thenReturn(Optional.empty());
        when(voterEligibilityPort.isEligibleToVote(null)).thenReturn(true);
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> voteService.vote(commandWithNullCpf));
    }

    @Test
    @DisplayName("Deve mascarar CPF corretamente quando CPF tem menos de 4 caracteres")
    void shouldMaskCpfCorrectlyWhenCpfHasLessThan4Characters() {
        String shortCpf = "123";
        VoteCommand commandWithShortCpf = new VoteCommand(agendaId, shortCpf, VoteChoice.YES);
        
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(openSession));
        when(voteRepository.findByAgendaIdAndCpf(agendaId, shortCpf)).thenReturn(Optional.empty());
        when(voterEligibilityPort.isEligibleToVote(shortCpf)).thenReturn(true);
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> voteService.vote(commandWithShortCpf));
    }
}

