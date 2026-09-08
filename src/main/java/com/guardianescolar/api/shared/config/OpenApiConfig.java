package com.guardianescolar.api.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI guardianEscolarOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Guardian Escolar API")
                        .version("0.0.1")
                        .description("API backend para Guardian Escolar."))
                .addServersItem(new Server().url("/api"));
    }
}
