package br.com.bank.voting.adapters.in.web;

import br.com.bank.voting.application.dto.command.CreateAgendaCommand;
import br.com.bank.voting.application.dto.result.AgendaCreatedResult;
import br.com.bank.voting.application.dto.result.VotingResultResult;
import br.com.bank.voting.application.port.in.CreateAgendaUseCase;
import br.com.bank.voting.application.port.in.GetResultUseCase;
import br.com.bank.voting.domain.model.enums.SessionStatus;
import br.com.bank.voting.domain.model.enums.VotingResult;
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
@DisplayName("AgendaController Tests")
class AgendaControllerTest {

    @Mock
    private CreateAgendaUseCase createAgendaUseCase;

    @Mock
    private GetResultUseCase getResultUseCase;

    @InjectMocks
    private AgendaController controller;

    private UUID agendaId;

    @BeforeEach
    void setUp() {
        agendaId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Deve criar pauta com sucesso")
    void shouldCreateAgendaSuccessfully() {
        CreateAgendaCommand command = new CreateAgendaCommand("Pauta Teste");
        AgendaCreatedResult result = new AgendaCreatedResult(agendaId, "Pauta Teste", LocalDateTime.now());

        when(createAgendaUseCase.create(any(CreateAgendaCommand.class))).thenReturn(result);

        ResponseEntity<AgendaCreatedResult> response = controller.createAgenda(command);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(agendaId, response.getBody().id());
        assertEquals("Pauta Teste", response.getBody().title());
        verify(createAgendaUseCase).create(command);
    }

    @Test
    @DisplayName("Deve obter resultado da votação com sucesso")
    void shouldGetResultSuccessfully() {
        VotingResultResult result = new VotingResultResult(
                agendaId,
                SessionStatus.CLOSED,
                10L,
                5L,
                15L,
                VotingResult.APPROVED
        );

        when(getResultUseCase.getResult(agendaId)).thenReturn(result);

        ResponseEntity<VotingResultResult> response = controller.getResult(agendaId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(agendaId, response.getBody().agendaId());
        assertEquals(SessionStatus.CLOSED, response.getBody().sessionStatus());
        assertEquals(10L, response.getBody().yes());
        assertEquals(5L, response.getBody().no());
        assertEquals(15L, response.getBody().total());
        assertEquals(VotingResult.APPROVED, response.getBody().result());
        verify(getResultUseCase).getResult(agendaId);
    }
}

