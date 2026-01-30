package br.com.bank.voting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("VotingSessionApiApplication Tests")
class VotingSessionApiApplicationTest {

    @Test
    @DisplayName("Deve carregar contexto da aplicação")
    void contextLoads() {
        // Teste verifica se o Spring Boot consegue carregar o contexto da aplicação
        // Isso garante que todas as configurações, beans e dependências estão corretas
    }
}

