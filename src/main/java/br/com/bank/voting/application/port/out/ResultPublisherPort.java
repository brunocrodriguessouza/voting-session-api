package br.com.bank.voting.application.port.out;

import br.com.bank.voting.application.dto.result.VotingResultResult;

public interface ResultPublisherPort {
    void publishResult(VotingResultResult result);
}

