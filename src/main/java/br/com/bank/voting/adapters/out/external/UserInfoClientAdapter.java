package br.com.bank.voting.adapters.out.external;

import br.com.bank.voting.application.port.out.VoterEligibilityPort;
import org.springframework.stereotype.Component;

/**
 * Adapter para integração com serviço externo de verificação de elegibilidade do eleitor.
 * Implementação placeholder - será implementada quando o bônus 1 for desenvolvido.
 */
@Component
public class UserInfoClientAdapter implements VoterEligibilityPort {

    @Override
    public boolean isEligibleToVote(String cpf) {
        // TODO: Implementar chamada HTTP para https://user-info.herokuapp.com/users/{cpf}
        // Por enquanto retorna true para permitir desenvolvimento sem a integração
        return true;
    }
}

