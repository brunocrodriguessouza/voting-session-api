package br.com.bank.voting.adapters.in.web;

import br.com.bank.voting.adapters.in.web.dto.PublishedResultResponse;
import br.com.bank.voting.adapters.out.messaging.InMemoryMessageQueue;
import br.com.bank.voting.application.dto.result.VotingResultResult;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessagingController Tests")
class MessagingControllerTest {

    @Mock
    private InMemoryMessageQueue messageQueue;

    @InjectMocks
    private MessagingController controller;

    private UUID agendaId;
    private VotingResultResult result;

    @BeforeEach
    void setUp() {
        agendaId = UUID.randomUUID();
        result = new VotingResultResult(
                agendaId,
                SessionStatus.CLOSED,
                10L,
                5L,
                15L,
                VotingResult.APPROVED
        );
    }

    @Test
    @DisplayName("Deve listar mensagens publicadas")
    void shouldListPublishedMessages() {
        InMemoryMessageQueue.PublishedResult publishedResult = 
                new InMemoryMessageQueue.PublishedResult(result, LocalDateTime.now());
        List<InMemoryMessageQueue.PublishedResult> messages = Arrays.asList(publishedResult);

        when(messageQueue.getAllMessages()).thenReturn(messages);

        ResponseEntity<List<PublishedResultResponse>> response = controller.getMessages();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(agendaId, response.getBody().get(0).result().agendaId());
        assertEquals(SessionStatus.CLOSED, response.getBody().get(0).result().sessionStatus());
        assertEquals(10L, response.getBody().get(0).result().yes());
        assertEquals(5L, response.getBody().get(0).result().no());
        assertEquals(VotingResult.APPROVED, response.getBody().get(0).result().result());
        assertNotNull(response.getBody().get(0).publishedAt());
        verify(messageQueue).getAllMessages();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver mensagens")
    void shouldReturnEmptyListWhenNoMessages() {
        when(messageQueue.getAllMessages()).thenReturn(List.of());

        ResponseEntity<List<PublishedResultResponse>> response = controller.getMessages();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(messageQueue).getAllMessages();
    }

    @Test
    @DisplayName("Deve retornar informações da fila")
    void shouldReturnQueueInfo() {
        when(messageQueue.getMessageCount()).thenReturn(5);

        ResponseEntity<Map<String, Object>> response = controller.getQueueInfo();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().get("messageCount"));
        assertEquals("In-Memory", response.getBody().get("queueType"));
        assertTrue(response.getBody().containsKey("description"));
        verify(messageQueue).getMessageCount();
    }
}

