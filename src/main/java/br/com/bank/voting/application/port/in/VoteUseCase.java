package br.com.bank.voting.application.port.in;

import br.com.bank.voting.application.dto.command.VoteCommand;

public interface VoteUseCase {
    void vote(VoteCommand command);
}


