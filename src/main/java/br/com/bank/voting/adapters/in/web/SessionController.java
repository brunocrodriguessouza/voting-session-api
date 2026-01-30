package br.com.bank.voting.adapters.in.web;

import br.com.bank.voting.application.dto.command.OpenSessionCommand;
import br.com.bank.voting.application.dto.result.SessionOpenedResult;
import br.com.bank.voting.application.port.in.OpenSessionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST para gerenciamento de sessões de votação.
 */
@RestController
@RequestMapping("/api/v1/agendas/{agendaId}/sessions")
@Tag(name = "Sessões", description = "Endpoints para gerenciamento de sessões de votação")
public class SessionController {

    private static final Logger log = LoggerFactory.getLogger(SessionController.class);

    private final OpenSessionUseCase openSessionUseCase;

    public SessionController(OpenSessionUseCase openSessionUseCase) {
        this.openSessionUseCase = openSessionUseCase;
    }

    /**
     * Abre uma sessão de votação para uma pauta.
     * Se a duração não for informada, será usada a duração padrão de 1 minuto.
     *
     * @param agendaId ID da pauta
     * @param durationMinutes duração da sessão em minutos (opcional, padrão: 1 minuto)
     * @return resposta HTTP 201 com os dados da sessão aberta
     */
    @PostMapping
    @Operation(summary = "Abrir sessão", description = "Abre uma sessão de votação para uma pauta. Duração padrão: 1 minuto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sessão aberta com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada"),
            @ApiResponse(responseCode = "409", description = "Já existe uma sessão aberta para esta pauta")
    })
    public ResponseEntity<SessionOpenedResult> openSession(
            @Parameter(description = "ID da pauta") @PathVariable UUID agendaId,
            @Parameter(description = "Duração da sessão em minutos (opcional, padrão: 1)") 
            @RequestParam(required = false) Integer durationMinutes) {
        
        log.info("Received request to open session for agenda: {} with duration: {} minutes", 
                agendaId, durationMinutes != null ? durationMinutes : "default (1)");
        
        OpenSessionCommand command = new OpenSessionCommand(agendaId, durationMinutes);
        SessionOpenedResult result = openSessionUseCase.open(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}

