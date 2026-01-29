package br.com.bank.voting.adapters.out.messaging;

import br.com.bank.voting.application.dto.result.VotingResultResult;
import br.com.bank.voting.application.port.out.ResultPublisherPort;
import org.springframework.stereotype.Component;

/**
 * Adapter para publicação de resultados via mensageria.
 * Implementação placeholder (noop) - será implementada quando o bônus 2 for desenvolvido.
 */
@Component
public class ResultPublisherAdapter implements ResultPublisherPort {

    @Override
    public void publishResult(VotingResultResult result) {
        // TODO: Implementar publicação via Kafka/RabbitMQ quando o bônus 2 for desenvolvido
    }
}

