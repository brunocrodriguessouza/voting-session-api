package br.com.bank.voting.adapters.out.messaging;

import br.com.bank.voting.application.dto.result.VotingResultResult;
import br.com.bank.voting.domain.model.enums.SessionStatus;
import br.com.bank.voting.domain.model.enums.VotingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InMemoryMessageQueue Tests")
class InMemoryMessageQueueTest {

    private InMemoryMessageQueue messageQueue;

    @BeforeEach
    void setUp() {
        messageQueue = new InMemoryMessageQueue();
    }

    @Test
    @DisplayName("Deve publicar mensagem na fila")
    void shouldPublishMessageToQueue() {
        VotingResultResult result = createTestResult();
        
        messageQueue.publish(result);
        
        assertEquals(1, messageQueue.getMessageCount());
    }

    @Test
    @DisplayName("Deve retornar todas as mensagens publicadas")
    void shouldReturnAllPublishedMessages() {
        VotingResultResult result1 = createTestResult();
        VotingResultResult result2 = createTestResult();
        
        messageQueue.publish(result1);
        messageQueue.publish(result2);
        
        List<InMemoryMessageQueue.PublishedResult> messages = messageQueue.getAllMessages();
        
        assertEquals(2, messages.size());
        assertEquals(result1, messages.get(0).result());
        assertEquals(result2, messages.get(1).result());
        assertNotNull(messages.get(0).publishedAt());
        assertNotNull(messages.get(1).publishedAt());
    }

    @Test
    @DisplayName("Deve retornar contagem correta de mensagens")
    void shouldReturnCorrectMessageCount() {
        assertEquals(0, messageQueue.getMessageCount());
        
        messageQueue.publish(createTestResult());
        assertEquals(1, messageQueue.getMessageCount());
        
        messageQueue.publish(createTestResult());
        assertEquals(2, messageQueue.getMessageCount());
    }

    @Test
    @DisplayName("Deve limpar a fila")
    void shouldClearQueue() {
        messageQueue.publish(createTestResult());
        messageQueue.publish(createTestResult());
        
        assertEquals(2, messageQueue.getMessageCount());
        
        messageQueue.clear();
        
        assertEquals(0, messageQueue.getMessageCount());
        assertTrue(messageQueue.getAllMessages().isEmpty());
    }

    @Test
    @DisplayName("Deve manter ordem de publicação (FIFO)")
    void shouldMaintainPublicationOrder() {
        VotingResultResult result1 = createTestResult(UUID.randomUUID());
        VotingResultResult result2 = createTestResult(UUID.randomUUID());
        VotingResultResult result3 = createTestResult(UUID.randomUUID());
        
        messageQueue.publish(result1);
        messageQueue.publish(result2);
        messageQueue.publish(result3);
        
        List<InMemoryMessageQueue.PublishedResult> messages = messageQueue.getAllMessages();
        
        assertEquals(3, messages.size());
        assertEquals(result1.agendaId(), messages.get(0).result().agendaId());
        assertEquals(result2.agendaId(), messages.get(1).result().agendaId());
        assertEquals(result3.agendaId(), messages.get(2).result().agendaId());
    }

    @Test
    @DisplayName("Deve incluir timestamp de publicação")
    void shouldIncludePublicationTimestamp() {
        VotingResultResult result = createTestResult();
        LocalDateTime beforePublish = LocalDateTime.now();
        
        messageQueue.publish(result);
        
        LocalDateTime afterPublish = LocalDateTime.now();
        List<InMemoryMessageQueue.PublishedResult> messages = messageQueue.getAllMessages();
        
        assertNotNull(messages.get(0).publishedAt());
        assertTrue(messages.get(0).publishedAt().isAfter(beforePublish.minusSeconds(1)));
        assertTrue(messages.get(0).publishedAt().isBefore(afterPublish.plusSeconds(1)));
    }

    @Test
    @DisplayName("Deve lidar com múltiplas publicações do mesmo resultado")
    void shouldHandleMultiplePublicationsOfSameResult() {
        VotingResultResult result = createTestResult();
        
        messageQueue.publish(result);
        messageQueue.publish(result);
        messageQueue.publish(result);
        
        assertEquals(3, messageQueue.getMessageCount());
        List<InMemoryMessageQueue.PublishedResult> messages = messageQueue.getAllMessages();
        
        assertEquals(3, messages.size());
        // Todas devem ter o mesmo resultado
        messages.forEach(msg -> assertEquals(result.agendaId(), msg.result().agendaId()));
    }

    @Test
    @DisplayName("Deve remover mensagem mais antiga quando fila estiver cheia")
    void shouldRemoveOldestMessageWhenQueueIsFull() {
        // Preencher a fila até a capacidade (1000)
        for (int i = 0; i < 1000; i++) {
            messageQueue.publish(createTestResult(UUID.randomUUID()));
        }
        
        assertEquals(1000, messageQueue.getMessageCount());
        
        // Adicionar mais uma mensagem (deve remover a mais antiga)
        VotingResultResult newResult = createTestResult(UUID.randomUUID());
        messageQueue.publish(newResult);
        
        // Ainda deve ter 1000 mensagens
        assertEquals(1000, messageQueue.getMessageCount());
        
        // A última mensagem deve ser a nova
        List<InMemoryMessageQueue.PublishedResult> messages = messageQueue.getAllMessages();
        assertEquals(newResult.agendaId(), messages.get(messages.size() - 1).result().agendaId());
    }

    private VotingResultResult createTestResult() {
        return createTestResult(UUID.randomUUID());
    }

    private VotingResultResult createTestResult(UUID agendaId) {
        return new VotingResultResult(
                agendaId,
                SessionStatus.CLOSED,
                10L,
                5L,
                15L,
                VotingResult.APPROVED
        );
    }
}

