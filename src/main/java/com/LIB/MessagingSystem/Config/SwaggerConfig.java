package com.LIB.MessagingSystem.Config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 *
 *  @author Abenezer Teshome  - Date 17/aug/2024
 *  A swagger configuration class for testing the API endpoints
 */
@Configuration
@OpenAPIDefinition
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI()
    {
        return new OpenAPI()
//                .servers(List.of(new Server()))
                .info(new Info().title("Document Sharing Service").version("1.0"));
    }
}
