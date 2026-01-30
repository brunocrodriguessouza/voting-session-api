package br.com.bank.voting.adapters.out.messaging;

import br.com.bank.voting.application.dto.result.VotingResultResult;
import br.com.bank.voting.domain.model.enums.SessionStatus;
import br.com.bank.voting.domain.model.enums.VotingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResultPublisherAdapter Tests")
class ResultPublisherAdapterTest {

    @Mock
    private InMemoryMessageQueue messageQueue;

    private ResultPublisherAdapter adapter;

    private VotingResultResult testResult;

    @BeforeEach
    void setUp() {
        // Criar adapter com mensageria habilitada
        adapter = new ResultPublisherAdapter(messageQueue, true);
        
        testResult = new VotingResultResult(
                UUID.randomUUID(),
                SessionStatus.CLOSED,
                10L,
                5L,
                15L,
                VotingResult.APPROVED
        );
    }

    @Test
    @DisplayName("Deve publicar resultado quando mensageria está habilitada")
    void shouldPublishResultWhenMessagingEnabled() {
        adapter.publishResult(testResult);
        
        verify(messageQueue, times(1)).publish(testResult);
    }

    @Test
    @DisplayName("Não deve publicar quando mensageria está desabilitada")
    void shouldNotPublishWhenMessagingDisabled() {
        ResultPublisherAdapter disabledAdapter = new ResultPublisherAdapter(messageQueue, false);
        
        disabledAdapter.publishResult(testResult);
        
        verify(messageQueue, never()).publish(any());
    }

    @Test
    @DisplayName("Deve tratar exceção sem quebrar o fluxo")
    void shouldHandleExceptionWithoutBreakingFlow() {
        doThrow(new RuntimeException("Queue error")).when(messageQueue).publish(any());
        
        // Não deve lançar exceção
        assertDoesNotThrow(() -> adapter.publishResult(testResult));
        
        verify(messageQueue, times(1)).publish(testResult);
    }

    @Test
    @DisplayName("Deve publicar múltiplos resultados")
    void shouldPublishMultipleResults() {
        VotingResultResult result1 = createTestResult();
        VotingResultResult result2 = createTestResult();
        
        adapter.publishResult(result1);
        adapter.publishResult(result2);
        
        verify(messageQueue, times(1)).publish(result1);
        verify(messageQueue, times(1)).publish(result2);
    }

    @Test
    @DisplayName("Deve publicar resultado com status CLOSED")
    void shouldPublishResultWithClosedStatus() {
        VotingResultResult closedResult = new VotingResultResult(
                UUID.randomUUID(),
                SessionStatus.CLOSED,
                5L,
                3L,
                8L,
                VotingResult.REJECTED
        );
        
        adapter.publishResult(closedResult);
        
        verify(messageQueue, times(1)).publish(closedResult);
    }

    @Test
    @DisplayName("Deve publicar resultado com status OPEN (caso edge)")
    void shouldPublishResultWithOpenStatus() {
        VotingResultResult openResult = new VotingResultResult(
                UUID.randomUUID(),
                SessionStatus.OPEN,
                2L,
                1L,
                3L,
                VotingResult.APPROVED
        );
        
        adapter.publishResult(openResult);
        
        verify(messageQueue, times(1)).publish(openResult);
    }

    private VotingResultResult createTestResult() {
        return new VotingResultResult(
                UUID.randomUUID(),
                SessionStatus.CLOSED,
                10L,
                5L,
                15L,
                VotingResult.APPROVED
        );
    }
}

