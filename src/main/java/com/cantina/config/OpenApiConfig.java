package com.cantina.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sistemaCantinaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema Cantina API")
                        .description("API para gerenciamento de cantina/restaurante")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Sistema Cantina")
                                .email("contato@sistemacantina.com")));
    }
}