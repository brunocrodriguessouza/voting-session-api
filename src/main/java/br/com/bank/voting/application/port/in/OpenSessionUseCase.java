package br.com.bank.voting.application.port.in;

import br.com.bank.voting.application.dto.command.OpenSessionCommand;
import br.com.bank.voting.application.dto.result.SessionOpenedResult;

public interface OpenSessionUseCase {
    SessionOpenedResult open(OpenSessionCommand command);
}

