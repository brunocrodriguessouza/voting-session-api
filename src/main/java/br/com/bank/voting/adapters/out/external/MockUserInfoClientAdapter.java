package br.com.bank.voting.adapters.out.external;

import br.com.bank.voting.application.port.out.VoterEligibilityPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Adapter mock para validação de elegibilidade (modo desenvolvimento).
 * Usado quando a validação externa está desabilitada.
 * Permite que qualquer CPF seja considerado elegível para testes.
 */
@Component
@ConditionalOnProperty(name = "voting.external.user-info.enabled", havingValue = "false", matchIfMissing = false)
public class MockUserInfoClientAdapter implements VoterEligibilityPort {

    private static final Logger log = LoggerFactory.getLogger(MockUserInfoClientAdapter.class);

    @Override
    public boolean isEligibleToVote(String cpf) {
        if (log.isWarnEnabled()) {
            log.warn("Mock adapter: Skipping external validation for CPF: {}", maskCpf(cpf));
            log.warn("In production, this should use the real external service!");
        }
        // Em modo mock, sempre retorna true (considera elegível)
        return true;
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 4) {
            return "***";
        }
        return "***" + cpf.substring(cpf.length() - 4);
    }
}


