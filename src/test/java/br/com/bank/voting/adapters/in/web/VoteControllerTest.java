package br.com.bank.voting.adapters.in.web;

import br.com.bank.voting.application.dto.command.VoteCommand;
import br.com.bank.voting.application.port.in.VoteUseCase;
import br.com.bank.voting.domain.model.enums.VoteChoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VoteController Tests")
class VoteControllerTest {

    @Mock
    private VoteUseCase voteUseCase;

    @InjectMocks
    private VoteController controller;

    private UUID agendaId;
    private String cpf;

    @BeforeEach
    void setUp() {
        agendaId = UUID.randomUUID();
        cpf = "12345678901";
    }

    @Test
    @DisplayName("Deve registrar voto SIM com sucesso")
    void shouldRegisterYesVoteSuccessfully() {
        VoteController.VoteRequest request = new VoteController.VoteRequest(cpf, VoteChoice.YES);

        doNothing().when(voteUseCase).vote(any(VoteCommand.class));

        ResponseEntity<Void> response = controller.vote(agendaId, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(voteUseCase).vote(any(VoteCommand.class));
    }

    @Test
    @DisplayName("Deve registrar voto NÃO com sucesso")
    void shouldRegisterNoVoteSuccessfully() {
        VoteController.VoteRequest request = new VoteController.VoteRequest(cpf, VoteChoice.NO);

        doNothing().when(voteUseCase).vote(any(VoteCommand.class));

        ResponseEntity<Void> response = controller.vote(agendaId, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(voteUseCase).vote(any(VoteCommand.class));
    }

    @Test
    @DisplayName("Deve criar VoteCommand corretamente")
    void shouldCreateVoteCommandCorrectly() {
        VoteController.VoteRequest request = new VoteController.VoteRequest(cpf, VoteChoice.YES);

        doNothing().when(voteUseCase).vote(any(VoteCommand.class));

        controller.vote(agendaId, request);

        verify(voteUseCase).vote(argThat(command ->
                command.agendaId().equals(agendaId) &&
                command.cpf().equals(cpf) &&
                command.choice() == VoteChoice.YES
        ));
    }
}
