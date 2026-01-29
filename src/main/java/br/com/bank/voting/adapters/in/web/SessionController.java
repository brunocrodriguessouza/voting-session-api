package br.com.bank.voting.adapters.in.web;

import br.com.bank.voting.application.dto.command.OpenSessionCommand;
import br.com.bank.voting.application.dto.result.SessionOpenedResult;
import br.com.bank.voting.application.port.in.OpenSessionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agendas/{agendaId}/sessions")
public class SessionController {

    private final OpenSessionUseCase openSessionUseCase;

    public SessionController(OpenSessionUseCase openSessionUseCase) {
        this.openSessionUseCase = openSessionUseCase;
    }

    @PostMapping
    public ResponseEntity<SessionOpenedResult> openSession(
            @PathVariable UUID agendaId,
            @RequestParam(required = false) Integer durationMinutes) {
        
        OpenSessionCommand command = new OpenSessionCommand(agendaId, durationMinutes);
        SessionOpenedResult result = openSessionUseCase.open(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}

