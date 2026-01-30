package br.com.bank.voting.adapters.out.external;

import br.com.bank.voting.adapters.out.external.dto.UserInfoResponse;
import br.com.bank.voting.adapters.out.external.exception.ExternalServiceUnavailableException;
import br.com.bank.voting.adapters.out.external.exception.InvalidCpfException;
import br.com.bank.voting.application.port.out.VoterEligibilityPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Duration;

/**
 * Adapter para integração com serviço externo de verificação de elegibilidade do eleitor.
 * Implementa a integração com a API: https://user-info.herokuapp.com/users/{cpf}
 */
@Component
@ConditionalOnProperty(name = "voting.external.user-info.enabled", havingValue = "true", matchIfMissing = true)
public class UserInfoClientAdapter implements VoterEligibilityPort {

    private static final Logger log = LoggerFactory.getLogger(UserInfoClientAdapter.class);
    
    private final RestClient restClient;
    private final String baseUrl;

    public UserInfoClientAdapter(
            @Value("${voting.external.user-info.url:https://user-info.herokuapp.com}") String baseUrl,
            @Value("${voting.external.user-info.timeout-seconds:5}") int timeoutSeconds) {
        this.baseUrl = baseUrl;
        // RestClient do Spring Boot 4.0.2 não suporta timeout direto no builder
        // O timeout será gerenciado pelo cliente HTTP subjacente
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Verifica se um associado é elegível para votar consultando o serviço externo.
     *
     * @param cpf CPF do associado (11 dígitos)
     * @return true se o associado pode votar (ABLE_TO_VOTE), false caso contrário
     * @throws InvalidCpfException se o CPF for inválido (API retorna 404)
     * @throws ExternalServiceUnavailableException se o serviço estiver indisponível
     */
    @Override
    public boolean isEligibleToVote(String cpf) {
        log.debug("Checking eligibility for CPF: {}", maskCpf(cpf));
        
        try {
            UserInfoResponse userInfoResponse = restClient.get()
                    .uri("/users/{cpf}", cpf)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (request, httpResponse) -> {
                        log.warn("CPF not found (404) in external service: {}", maskCpf(cpf));
                        throw new InvalidCpfException(cpf);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, httpResponse) -> {
                        log.error("Server error from user-info service: {}", httpResponse.getStatusCode());
                        throw new ExternalServiceUnavailableException("Server error: " + httpResponse.getStatusCode(), null);
                    })
                    .body(UserInfoResponse.class);

            if (userInfoResponse == null) {
                log.warn("Null response from user-info service for CPF: {}", maskCpf(cpf));
                throw new ExternalServiceUnavailableException("Null response from service", null);
            }

            boolean eligible = UserInfoResponse.ABLE_TO_VOTE.equals(userInfoResponse.status());
            log.info("Eligibility check for CPF {}: {}", maskCpf(cpf), eligible ? "ABLE_TO_VOTE" : "UNABLE_TO_VOTE");
            
            return eligible;

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("CPF not found (404) in external service: {}", maskCpf(cpf));
            throw new InvalidCpfException(cpf);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                log.warn("CPF not found (404) in external service: {}", maskCpf(cpf));
                throw new InvalidCpfException(cpf);
            }
            log.error("HTTP client error from user-info service: {}", e.getStatusCode());
            throw new ExternalServiceUnavailableException("HTTP error: " + e.getStatusCode(), e);
        } catch (InvalidCpfException e) {
            throw e;
        } catch (ExternalServiceUnavailableException e) {
            throw e;
        } catch (ResourceAccessException e) {
            log.error("Network/timeout error calling user-info service for CPF: {}", maskCpf(cpf), e);
            throw new ExternalServiceUnavailableException("Network or timeout error: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error calling user-info service for CPF: {}", maskCpf(cpf), e);
            throw new ExternalServiceUnavailableException("Unexpected error: " + e.getMessage(), e);
        }
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 4) {
            return "***";
        }
        return "***" + cpf.substring(cpf.length() - 4);
    }
}

