package br.com.bank.voting.adapters.in.web;

import br.com.bank.voting.adapters.in.web.dto.PublishedResultResponse;
import br.com.bank.voting.adapters.out.messaging.InMemoryMessageQueue;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller REST para visualização de mensagens publicadas na fila (Bônus 2).
 * Endpoint de demonstração para mostrar que os resultados estão sendo publicados.
 */
@RestController
@RequestMapping("/api/v1/messaging")
@Tag(name = "Messaging", description = "Endpoints para visualização de mensagens publicadas (Bônus 2)")
public class MessagingController {

    private static final Logger log = LoggerFactory.getLogger(MessagingController.class);

    private final InMemoryMessageQueue messageQueue;

    public MessagingController(InMemoryMessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    /**
     * Retorna todas as mensagens publicadas na fila in-memory.
     * Útil para demonstração do Bônus 2 sem necessidade de infraestrutura externa.
     *
     * @return lista de resultados publicados
     */
    @GetMapping("/messages")
    @Operation(summary = "Listar mensagens", description = "Retorna todas as mensagens publicadas na fila in-memory (Bônus 2)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mensagens retornada com sucesso")
    })
    public ResponseEntity<List<PublishedResultResponse>> getMessages() {
        log.info("Retrieving all messages from queue");
        List<PublishedResultResponse> messages = messageQueue.getAllMessages().stream()
                .map(published -> new PublishedResultResponse(
                        published.result(),
                        published.publishedAt()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(messages);
    }

    /**
     * Retorna informações sobre a fila de mensagens.
     *
     * @return informações da fila (quantidade de mensagens)
     */
    @GetMapping("/queue-info")
    @Operation(summary = "Informações da fila", description = "Retorna informações sobre a fila de mensagens (quantidade de mensagens, tipo de fila)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações da fila retornadas com sucesso")
    })
    public ResponseEntity<Map<String, Object>> getQueueInfo() {
        int messageCount = messageQueue.getMessageCount();
        return ResponseEntity.ok(Map.of(
                "messageCount", messageCount,
                "queueType", "In-Memory",
                "description", "Fila in-memory para demonstração do Bônus 2"
        ));
    }
}

