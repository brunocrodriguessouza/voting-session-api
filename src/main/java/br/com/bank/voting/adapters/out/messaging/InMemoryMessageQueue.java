package br.com.bank.voting.adapters.out.messaging;

import br.com.bank.voting.application.dto.result.VotingResultResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Fila in-memory para armazenar resultados de votação publicados.
 * Utilizada para demonstração do Bônus 2 sem necessidade de infraestrutura externa.
 */
@Component
public class InMemoryMessageQueue {

    private final BlockingQueue<PublishedResult> queue;
    private final int capacity;

    public InMemoryMessageQueue() {
        this.capacity = 1000;
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    /**
     * Publica um resultado na fila.
     *
     * @param result resultado da votação a ser publicado
     */
    public void publish(VotingResultResult result) {
        PublishedResult publishedResult = new PublishedResult(
                result,
                LocalDateTime.now()
        );
        
        if (!queue.offer(publishedResult)) {
            // Se a fila estiver cheia, remove o mais antigo e adiciona o novo
            queue.poll();
            queue.offer(publishedResult);
        }
    }

    /**
     * Retorna todas as mensagens publicadas (para visualização/demonstração).
     *
     * @return lista de resultados publicados
     */
    public List<PublishedResult> getAllMessages() {
        return new ArrayList<>(queue);
    }

    /**
     * Retorna a quantidade de mensagens na fila.
     *
     * @return número de mensagens
     */
    public int getMessageCount() {
        return queue.size();
    }

    /**
     * Limpa a fila (útil para testes).
     */
    public void clear() {
        queue.clear();
    }

    /**
     * Representa um resultado publicado na fila com timestamp.
     */
    public record PublishedResult(
            VotingResultResult result,
            LocalDateTime publishedAt
    ) {
    }
}


