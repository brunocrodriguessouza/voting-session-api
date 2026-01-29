package br.com.bank.voting.adapters.in.web;

import br.com.bank.voting.application.dto.command.CreateAgendaCommand;
import br.com.bank.voting.application.dto.result.AgendaCreatedResult;
import br.com.bank.voting.application.port.in.CreateAgendaUseCase;
import br.com.bank.voting.application.port.in.GetResultUseCase;
import br.com.bank.voting.application.dto.result.VotingResultResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agendas")
public class AgendaController {

    private final CreateAgendaUseCase createAgendaUseCase;
    private final GetResultUseCase getResultUseCase;

    public AgendaController(CreateAgendaUseCase createAgendaUseCase, GetResultUseCase getResultUseCase) {
        this.createAgendaUseCase = createAgendaUseCase;
        this.getResultUseCase = getResultUseCase;
    }

    @PostMapping
    public ResponseEntity<AgendaCreatedResult> createAgenda(@Valid @RequestBody CreateAgendaCommand command) {
        AgendaCreatedResult result = createAgendaUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{agendaId}/result")
    public ResponseEntity<VotingResultResult> getResult(@PathVariable UUID agendaId) {
        VotingResultResult result = getResultUseCase.getResult(agendaId);
        return ResponseEntity.ok(result);
    }
}

