package br.com.bank.voting.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do OpenAPI (Swagger) para documentação da API.
 */
@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info();
        info.setTitle("Voting Session API");
        info.setVersion("1.0.0");
        info.setDescription("API REST para gerenciamento de sessões de votação em assembleias cooperativas. " +
                "Permite criar pautas, abrir sessões de votação, registrar votos e obter resultados.");
        
        return new OpenAPI().info(info);
    }
}

