package br.com.bank.voting.adapters.out.external;

import br.com.bank.voting.adapters.out.external.dto.UserInfoResponse;
import br.com.bank.voting.adapters.out.external.exception.ExternalServiceUnavailableException;
import br.com.bank.voting.adapters.out.external.exception.InvalidCpfException;
import br.com.bank.voting.application.port.out.VoterEligibilityPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Adapter para integração com serviço externo de verificação de elegibilidade do eleitor.
 * Implementa a integração com a API: https://user-info.herokuapp.com/users/{cpf}
 */
@Component
public class UserInfoClientAdapter implements VoterEligibilityPort {

    private static final Logger log = LoggerFactory.getLogger(UserInfoClientAdapter.class);
    
    private final RestClient restClient;
    private final String baseUrl;

    public UserInfoClientAdapter(
            @Value("${voting.external.user-info.url:https://user-info.herokuapp.com}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new HttpClientErrorException(response.getStatusCode());
                })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new HttpServerErrorException(response.getStatusCode());
                })
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
            UserInfoResponse response = restClient.get()
                    .uri("/users/{cpf}", cpf)
                    .retrieve()
                    .body(UserInfoResponse.class);

            if (response == null) {
                log.warn("Null response from user-info service for CPF: {}", maskCpf(cpf));
                throw new ExternalServiceUnavailableException("Null response from service", null);
            }

            boolean eligible = UserInfoResponse.ABLE_TO_VOTE.equals(response.status());
            log.info("Eligibility check for CPF {}: {}", maskCpf(cpf), eligible ? "ABLE_TO_VOTE" : "UNABLE_TO_VOTE");
            
            return eligible;

        } catch (HttpClientErrorException.NotFound e) {
            log.error("Invalid CPF: {}", maskCpf(cpf));
            throw new InvalidCpfException(cpf);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error from user-info service: {}", e.getStatusCode());
            throw new ExternalServiceUnavailableException("HTTP error: " + e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("Error calling user-info service for CPF: {}", maskCpf(cpf), e);
            throw new ExternalServiceUnavailableException("Service error", e);
        }
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 4) {
            return "***";
        }
        return "***" + cpf.substring(cpf.length() - 4);
    }
}

