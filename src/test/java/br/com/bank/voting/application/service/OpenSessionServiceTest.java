package br.com.bank.voting.application.service;

import br.com.bank.voting.application.dto.command.OpenSessionCommand;
import br.com.bank.voting.application.dto.result.SessionOpenedResult;
import br.com.bank.voting.application.port.out.AgendaRepositoryPort;
import br.com.bank.voting.application.port.out.SessionRepositoryPort;
import br.com.bank.voting.domain.model.Agenda;
import br.com.bank.voting.domain.model.VotingSession;
import br.com.bank.voting.domain.model.enums.SessionStatus;
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
@DisplayName("OpenSessionService Tests")
class OpenSessionServiceTest {

    @Mock
    private SessionRepositoryPort sessionRepository;

    @Mock
    private AgendaRepositoryPort agendaRepository;

    @InjectMocks
    private OpenSessionService openSessionService;

    private UUID agendaId;
    private Agenda agenda;
    private VotingSession savedSession;

    @BeforeEach
    void setUp() {
        agendaId = UUID.randomUUID();
        agenda = new Agenda(agendaId, "Pauta Teste", LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve abrir sessão com duração padrão de 1 minuto quando durationMinutes for null")
    void shouldOpenSessionWithDefaultDurationWhenDurationMinutesIsNull() {
        OpenSessionCommand command = new OpenSessionCommand(agendaId, null);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime closesAt = now.plusMinutes(1);

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.empty());
        when(sessionRepository.save(any(VotingSession.class))).thenAnswer(invocation -> {
            VotingSession session = invocation.getArgument(0);
            savedSession = new VotingSession(UUID.randomUUID(), session.getAgendaId(), session.getOpenedAt(), session.getClosesAt());
            return savedSession;
        });

        SessionOpenedResult result = openSessionService.open(command);

        assertNotNull(result);
        assertEquals(SessionStatus.OPEN, result.status());
        assertEquals(agendaId, result.agendaId());
        assertNotNull(result.openedAt());
        assertNotNull(result.closesAt());
        assertTrue(result.closesAt().isAfter(result.openedAt()));
        verify(sessionRepository).save(argThat(session -> 
            session.getClosesAt().equals(session.getOpenedAt().plusMinutes(1))
        ));
    }

    @Test
    @DisplayName("Deve abrir sessão com duração customizada quando durationMinutes for informado")
    void shouldOpenSessionWithCustomDurationWhenDurationMinutesIsProvided() {
        int customDuration = 5;
        OpenSessionCommand command = new OpenSessionCommand(agendaId, customDuration);
        LocalDateTime now = LocalDateTime.now();

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.empty());
        when(sessionRepository.save(any(VotingSession.class))).thenAnswer(invocation -> {
            VotingSession session = invocation.getArgument(0);
            savedSession = new VotingSession(UUID.randomUUID(), session.getAgendaId(), session.getOpenedAt(), session.getClosesAt());
            return savedSession;
        });

        SessionOpenedResult result = openSessionService.open(command);

        assertNotNull(result);
        assertEquals(SessionStatus.OPEN, result.status());
        verify(sessionRepository).save(argThat(session -> 
            session.getClosesAt().equals(session.getOpenedAt().plusMinutes(customDuration))
        ));
    }

    @Test
    @DisplayName("Deve lançar exceção quando pauta não existir")
    void shouldThrowExceptionWhenAgendaNotFound() {
        OpenSessionCommand command = new OpenSessionCommand(agendaId, null);

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> openSessionService.open(command));

        assertEquals("Agenda not found: " + agendaId, exception.getMessage());
        verify(sessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando já existir sessão aberta para a pauta")
    void shouldThrowExceptionWhenSessionAlreadyOpen() {
        OpenSessionCommand command = new OpenSessionCommand(agendaId, null);
        VotingSession existingSession = new VotingSession(
            UUID.randomUUID(), agendaId, LocalDateTime.now(), LocalDateTime.now().plusMinutes(1)
        );

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(existingSession));

        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> openSessionService.open(command));

        assertEquals("Session already open for agenda: " + agendaId, exception.getMessage());
        verify(sessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando durationMinutes for zero")
    void shouldThrowExceptionWhenDurationMinutesIsZero() {
        OpenSessionCommand command = new OpenSessionCommand(agendaId, 0);

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> openSessionService.open(command));

        assertEquals("Duration must be positive", exception.getMessage());
        verify(sessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando durationMinutes for negativo")
    void shouldThrowExceptionWhenDurationMinutesIsNegative() {
        OpenSessionCommand command = new OpenSessionCommand(agendaId, -1);

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> openSessionService.open(command));

        assertEquals("Duration must be positive", exception.getMessage());
        verify(sessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve calcular corretamente o horário de fechamento")
    void shouldCalculateClosingTimeCorrectly() {
        int duration = 3;
        OpenSessionCommand command = new OpenSessionCommand(agendaId, duration);

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.empty());
        when(sessionRepository.save(any(VotingSession.class))).thenAnswer(invocation -> {
            VotingSession session = invocation.getArgument(0);
            return new VotingSession(UUID.randomUUID(), session.getAgendaId(), session.getOpenedAt(), session.getClosesAt());
        });

        SessionOpenedResult result = openSessionService.open(command);

        long minutesDifference = java.time.Duration.between(result.openedAt(), result.closesAt()).toMinutes();
        assertEquals(duration, minutesDifference);
    }
}

