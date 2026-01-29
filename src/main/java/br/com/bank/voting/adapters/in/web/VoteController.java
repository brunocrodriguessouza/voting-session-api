package br.com.bank.voting.adapters.in.web;

import br.com.bank.voting.application.dto.command.VoteCommand;
import br.com.bank.voting.application.port.in.VoteUseCase;
import br.com.bank.voting.domain.model.enums.VoteChoice;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agendas/{agendaId}/votes")
public class VoteController {

    private final VoteUseCase voteUseCase;

    public VoteController(VoteUseCase voteUseCase) {
        this.voteUseCase = voteUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> vote(
            @PathVariable UUID agendaId,
            @Valid @RequestBody VoteRequest request) {
        
        VoteCommand command = new VoteCommand(
                agendaId,
                request.cpf(),
                request.choice()
        );
        
        voteUseCase.vote(command);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public record VoteRequest(
            String cpf,
            VoteChoice choice
    ) {
    }
}

