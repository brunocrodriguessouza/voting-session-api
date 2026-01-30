package br.com.bank.voting.adapters.out.messaging;

import br.com.bank.voting.application.dto.result.VotingResultResult;
import br.com.bank.voting.application.port.out.ResultPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Adapter para publicação de resultados via mensageria.
 * Implementa publicação em fila in-memory para demonstração do Bônus 2.
 * Em produção, pode ser substituído por implementação com Kafka/RabbitMQ.
 */
@Component
public class ResultPublisherAdapter implements ResultPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(ResultPublisherAdapter.class);

    private final InMemoryMessageQueue messageQueue;
    private final boolean messagingEnabled;

    public ResultPublisherAdapter(
            InMemoryMessageQueue messageQueue,
            @Value("${voting.messaging.enabled:true}") boolean messagingEnabled) {
        this.messageQueue = messageQueue;
        this.messagingEnabled = messagingEnabled;
    }

    /**
     * Publica o resultado da votação na fila de mensageria.
     * Se a mensageria estiver desabilitada, apenas loga a mensagem.
     *
     * @param result resultado da votação a ser publicado
     */
    @Override
    public void publishResult(VotingResultResult result) {
        if (!messagingEnabled) {
            log.debug("Messaging is disabled. Result not published: {}", result);
            return;
        }

        try {
            messageQueue.publish(result);
            log.info("Voting result published to queue. Agenda: {}, Result: {}, Yes: {}, No: {}", 
                    result.agendaId(), result.result(), result.yes(), result.no());
        } catch (Exception e) {
            log.error("Error publishing result to queue for agenda: {}", result.agendaId(), e);
            // Não lança exceção para não quebrar o fluxo principal
        }
    }
}

