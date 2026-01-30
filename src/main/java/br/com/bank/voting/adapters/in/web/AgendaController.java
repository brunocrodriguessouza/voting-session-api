package br.com.bank.voting.adapters.in.web;

import br.com.bank.voting.application.dto.command.CreateAgendaCommand;
import br.com.bank.voting.application.dto.result.AgendaCreatedResult;
import br.com.bank.voting.application.port.in.CreateAgendaUseCase;
import br.com.bank.voting.application.port.in.GetResultUseCase;
import br.com.bank.voting.application.dto.result.VotingResultResult;
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
 * Controller REST para gerenciamento de pautas de votação.
 */
@RestController
@RequestMapping("/api/v1/agendas")
@Tag(name = "Agendas", description = "Endpoints para gerenciamento de pautas de votação")
public class AgendaController {

    private static final Logger log = LoggerFactory.getLogger(AgendaController.class);

    private final CreateAgendaUseCase createAgendaUseCase;
    private final GetResultUseCase getResultUseCase;

    public AgendaController(CreateAgendaUseCase createAgendaUseCase, GetResultUseCase getResultUseCase) {
        this.createAgendaUseCase = createAgendaUseCase;
        this.getResultUseCase = getResultUseCase;
    }

    /**
     * Cria uma nova pauta de votação.
     *
     * @param command comando contendo o título da pauta
     * @return resposta HTTP 201 com os dados da pauta criada
     */
    @PostMapping
    @Operation(summary = "Criar pauta", description = "Cria uma nova pauta para votação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<AgendaCreatedResult> createAgenda(@Valid @RequestBody CreateAgendaCommand command) {
        log.info("Received request to create agenda with title: {}", command.title());
        AgendaCreatedResult result = createAgendaUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Obtém o resultado da votação de uma pauta.
     *
     * @param agendaId ID da pauta
     * @return resposta HTTP 200 com o resultado da votação
     */
    @GetMapping("/{agendaId}/result")
    @Operation(summary = "Obter resultado", description = "Retorna o resultado da votação de uma pauta. Se a sessão estiver fechada, o resultado é automaticamente publicado na fila (Bônus 2)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    })
    public ResponseEntity<VotingResultResult> getResult(
            @Parameter(description = "ID da pauta") @PathVariable UUID agendaId) {
        log.info("Received request to get result for agenda: {}", agendaId);
        VotingResultResult result = getResultUseCase.getResult(agendaId);
        return ResponseEntity.ok(result);
    }
}

