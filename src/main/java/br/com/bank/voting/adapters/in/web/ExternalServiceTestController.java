package br.com.bank.voting.adapters.in.web;

import br.com.bank.voting.adapters.out.external.dto.UserInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller de teste para verificar CPFs no serviço externo.
 * Útil para descobrir quais CPFs existem na API externa.
 */
@RestController
@RequestMapping("/api/v1/test")
@Tag(name = "Test", description = "Endpoints de teste (apenas desenvolvimento)")
public class ExternalServiceTestController {

    private static final Logger log = LoggerFactory.getLogger(ExternalServiceTestController.class);
    
    private static final String KEY_EXISTS = "exists";
    private static final String KEY_MESSAGE = "message";
    
    private final RestClient restClient;

    public ExternalServiceTestController() {
        this.restClient = RestClient.builder()
                .baseUrl("https://user-info.herokuapp.com")
                .build();
    }

    /**
     * Testa um CPF no serviço externo para verificar se existe e se é elegível.
     *
     * @param cpf CPF a ser testado
     * @return informações sobre o CPF (existe, elegível, etc.)
     */
    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Testar CPF no serviço externo", 
               description = "Verifica se um CPF existe no serviço externo e se é elegível para votar")
    public ResponseEntity<Map<String, Object>> testCpf(
            @Parameter(description = "CPF a ser testado (11 dígitos)") 
            @PathVariable String cpf) {
        
        if (log.isInfoEnabled()) {
            log.info("Testing CPF in external service: {}", maskCpf(cpf));
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("cpf", maskCpf(cpf));
        
        try {
            UserInfoResponse response = restClient.get()
                    .uri("/users/{cpf}", cpf)
                    .retrieve()
                    .body(UserInfoResponse.class);
            
            if (response != null) {
                result.put(KEY_EXISTS, true);
                result.put("status", response.status());
                result.put("eligible", "ABLE_TO_VOTE".equals(response.status()));
                result.put(KEY_MESSAGE, "CPF encontrado no serviço externo");
            } else {
                result.put(KEY_EXISTS, false);
                result.put(KEY_MESSAGE, "Resposta nula do serviço externo");
            }
            
            return ResponseEntity.ok(result);
            
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            result.put(KEY_EXISTS, false);
            result.put("status", "NOT_FOUND");
            result.put("eligible", false);
            result.put(KEY_MESSAGE, "CPF não encontrado no serviço externo (404)");
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put(KEY_EXISTS, false);
            result.put("error", e.getClass().getSimpleName());
            result.put(KEY_MESSAGE, "Erro ao consultar serviço externo: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 4) {
            return "***";
        }
        return "***" + cpf.substring(cpf.length() - 4);
    }
}

