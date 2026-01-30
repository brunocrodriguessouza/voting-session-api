package br.com.bank.voting.adapters.in.web;

import br.com.bank.voting.application.dto.command.VoteCommand;
import br.com.bank.voting.application.port.in.VoteUseCase;
import br.com.bank.voting.domain.model.enums.VoteChoice;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST para registro de votos.
 */
@RestController
@RequestMapping("/api/v1/agendas/{agendaId}/votes")
@Tag(name = "Votos", description = "Endpoints para registro de votos")
public class VoteController {

    private static final Logger log = LoggerFactory.getLogger(VoteController.class);

    private final VoteUseCase voteUseCase;

    public VoteController(VoteUseCase voteUseCase) {
        this.voteUseCase = voteUseCase;
    }

    /**
     * Registra um voto de um associado em uma pauta.
     * Cada associado pode votar apenas uma vez por pauta.
     *
     * @param agendaId ID da pauta
     * @param request requisição contendo o CPF do associado e a escolha (SIM/NÃO)
     * @return resposta HTTP 204 (No Content) em caso de sucesso
     */
    @PostMapping
    @Operation(summary = "Registrar voto", description = "Registra um voto (SIM ou NÃO) de um associado em uma pauta. Valida elegibilidade via serviço externo (Bônus 1)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Voto registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "CPF inválido ou dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Associado não elegível para votar"),
            @ApiResponse(responseCode = "404", description = "Pauta ou sessão não encontrada"),
            @ApiResponse(responseCode = "409", description = "Associado já votou nesta pauta"),
            @ApiResponse(responseCode = "503", description = "Serviço externo de validação indisponível")
    })
    public ResponseEntity<Void> vote(
            @Parameter(description = "ID da pauta") @PathVariable UUID agendaId,
            @Valid @RequestBody VoteRequest request) {
        
        log.info("Received vote request for agenda: {}, choice: {}", agendaId, request.choice());
        
        VoteCommand command = new VoteCommand(
                agendaId,
                request.cpf(),
                request.choice()
        );
        
        voteUseCase.vote(command);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Request DTO para registro de voto.
     *
     * @param cpf CPF do associado (11 dígitos)
     * @param choice escolha do voto (YES ou NO)
     */
    public record VoteRequest(
            String cpf,
            VoteChoice choice
    ) {
    }
}

