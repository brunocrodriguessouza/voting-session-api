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

/**
 * Adapter para integração com serviço externo de verificação de elegibilidade do eleitor.
 * Implementa a integração com a API: https://user-info.herokuapp.com/users/{cpf}
 */
@Component
@ConditionalOnProperty(name = "voting.external.user-info.enabled", havingValue = "true", matchIfMissing = true)
public class UserInfoClientAdapter implements VoterEligibilityPort {

    private static final Logger log = LoggerFactory.getLogger(UserInfoClientAdapter.class);
    
    private static final String CPF_NOT_FOUND_MESSAGE = "CPF not found (404) in external service: {}";
    
    private final RestClient restClient;

    public UserInfoClientAdapter(
            @Value("${voting.external.user-info.url:https://user-info.herokuapp.com}") String baseUrl) {
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
        if (log.isDebugEnabled()) {
            log.debug("Checking eligibility for CPF: {}", maskCpf(cpf));
        }
        
        try {
            UserInfoResponse userInfoResponse = fetchUserInfo(cpf);
            return processUserInfoResponse(userInfoResponse, cpf);
        } catch (InvalidCpfException | ExternalServiceUnavailableException e) {
            logAndRethrowException(e, cpf);
            throw e;
        } catch (HttpClientErrorException e) {
            handleHttpClientError(e, cpf);
            return false; // Never reached
        } catch (ResourceAccessException e) {
            handleNetworkError(e, cpf);
            return false; // Never reached
        } catch (Exception e) {
            handleUnexpectedError(e, cpf);
            return false; // Never reached
        }
    }

    private UserInfoResponse fetchUserInfo(String cpf) {
        return restClient.get()
                .uri("/users/{cpf}", cpf)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, httpResponse) -> {
                    if (log.isWarnEnabled()) {
                        log.warn(CPF_NOT_FOUND_MESSAGE, maskCpf(cpf));
                    }
                    throw new InvalidCpfException(cpf);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, httpResponse) -> {
                    if (log.isErrorEnabled()) {
                        log.error("Server error from user-info service: {}", httpResponse.getStatusCode());
                    }
                    throw new ExternalServiceUnavailableException("Server error: " + httpResponse.getStatusCode(), null);
                })
                .body(UserInfoResponse.class);
    }

    private boolean processUserInfoResponse(UserInfoResponse userInfoResponse, String cpf) {
        if (userInfoResponse == null) {
            if (log.isWarnEnabled()) {
                log.warn("Null response from user-info service for CPF: {}", maskCpf(cpf));
            }
            throw new ExternalServiceUnavailableException("Null response from service", null);
        }

        boolean eligible = UserInfoResponse.ABLE_TO_VOTE.equals(userInfoResponse.status());
        if (log.isInfoEnabled()) {
            log.info("Eligibility check for CPF {}: {}", maskCpf(cpf), eligible ? "ABLE_TO_VOTE" : "UNABLE_TO_VOTE");
        }
        
        return eligible;
    }

    private void handleHttpClientError(HttpClientErrorException e, String cpf) {
        if (e.getStatusCode().value() == 404) {
            if (log.isWarnEnabled()) {
                log.warn(CPF_NOT_FOUND_MESSAGE, maskCpf(cpf));
            }
            throw new InvalidCpfException(cpf);
        }
        if (log.isErrorEnabled()) {
            log.error("HTTP client error from user-info service: {}", e.getStatusCode(), e);
        }
        throw new ExternalServiceUnavailableException("HTTP error: " + e.getStatusCode(), e);
    }

    private void handleNetworkError(ResourceAccessException e, String cpf) {
        if (log.isErrorEnabled()) {
            log.error("Network/timeout error calling user-info service for CPF: {}", maskCpf(cpf), e);
        }
        throw new ExternalServiceUnavailableException("Network or timeout error: " + e.getMessage(), e);
    }

    private void handleUnexpectedError(Exception e, String cpf) {
        if (log.isErrorEnabled()) {
            log.error("Unexpected error calling user-info service for CPF: {}", maskCpf(cpf), e);
        }
        throw new ExternalServiceUnavailableException("Unexpected error: " + e.getMessage(), e);
    }

    private void logAndRethrowException(RuntimeException e, String cpf) {
        if (e instanceof InvalidCpfException) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid CPF detected: {}", maskCpf(cpf));
            }
        } else {
            if (log.isErrorEnabled()) {
                log.error("External service error for CPF: {}", maskCpf(cpf), e);
            }
        }
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 4) {
            return "***";
        }
        return "***" + cpf.substring(cpf.length() - 4);
    }
}

