package br.com.bank.voting.adapters.in.web;

import br.com.bank.voting.application.dto.command.OpenSessionCommand;
import br.com.bank.voting.application.dto.result.SessionOpenedResult;
import br.com.bank.voting.application.port.in.OpenSessionUseCase;
import br.com.bank.voting.domain.model.enums.SessionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionController Tests")
class SessionControllerTest {

    @Mock
    private OpenSessionUseCase openSessionUseCase;

    @InjectMocks
    private SessionController controller;

    private UUID agendaId;
    private UUID sessionId;

    @BeforeEach
    void setUp() {
        agendaId = UUID.randomUUID();
        sessionId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Deve abrir sessão com duração padrão")
    void shouldOpenSessionWithDefaultDuration() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime closesAt = now.plusMinutes(1);

        SessionOpenedResult result = new SessionOpenedResult(
                sessionId,
                agendaId,
                now,
                closesAt,
                SessionStatus.OPEN
        );

        when(openSessionUseCase.open(any(OpenSessionCommand.class))).thenReturn(result);

        ResponseEntity<SessionOpenedResult> response = controller.openSession(agendaId, null);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sessionId, response.getBody().sessionId());
        assertEquals(agendaId, response.getBody().agendaId());
        assertEquals(SessionStatus.OPEN, response.getBody().status());
        verify(openSessionUseCase).open(any(OpenSessionCommand.class));
    }

    @Test
    @DisplayName("Deve abrir sessão com duração customizada")
    void shouldOpenSessionWithCustomDuration() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime closesAt = now.plusMinutes(5);

        SessionOpenedResult result = new SessionOpenedResult(
                sessionId,
                agendaId,
                now,
                closesAt,
                SessionStatus.OPEN
        );

        when(openSessionUseCase.open(any(OpenSessionCommand.class))).thenReturn(result);

        ResponseEntity<SessionOpenedResult> response = controller.openSession(agendaId, 5);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sessionId, response.getBody().sessionId());
        assertEquals(SessionStatus.OPEN, response.getBody().status());
        verify(openSessionUseCase).open(any(OpenSessionCommand.class));
    }
}
