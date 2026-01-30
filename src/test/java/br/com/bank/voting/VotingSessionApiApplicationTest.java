package br.com.bank.voting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("VotingSessionApiApplication Tests")
class VotingSessionApiApplicationTest {

    @Test
    @DisplayName("Deve carregar contexto da aplicação")
    void contextLoads() {
        // Teste verifica se o Spring Boot consegue carregar o contexto da aplicação
        // Isso garante que todas as configurações, beans e dependências estão corretas
        // Este teste garante que a classe VotingSessionApiApplication pode ser iniciada
        assert VotingSessionApiApplication.class != null;
    }

    @Test
    @DisplayName("Deve ter método main")
    void shouldHaveMainMethod() {
        // Verifica que a classe tem o método main
        java.lang.reflect.Method[] methods = VotingSessionApiApplication.class.getDeclaredMethods();
        assertTrue(methods.length > 0, "A classe deve ter pelo menos um método");
        
        // Verifica que existe o método main
        boolean hasMainMethod = java.util.Arrays.stream(methods)
                .anyMatch(m -> m.getName().equals("main") && 
                              m.getParameterCount() == 1 && 
                              m.getParameterTypes()[0].equals(String[].class));
        assertTrue(hasMainMethod, "A classe deve ter o método main(String[] args)");
    }
}

