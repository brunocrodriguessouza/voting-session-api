package br.com.bank.voting.application.port.in;

import br.com.bank.voting.application.dto.command.CreateAgendaCommand;
import br.com.bank.voting.application.dto.result.AgendaCreatedResult;

public interface CreateAgendaUseCase {
    AgendaCreatedResult create(CreateAgendaCommand command);
}


