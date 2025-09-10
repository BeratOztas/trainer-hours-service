package com.epam.gym.trainer_hours_service.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {
   
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().openapi("3.0.1")
                .info(new Info().title("Gym Trainer Workload API (OpenAPI 3)").version("1.0.0")
                        .description("Spring Boot REST API for managing trainer workloads and training events.")
                        .contact(new Contact().name("Berat").email("berat.oztas.dev@gmail.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder().group("trainer-workload-public").pathsToMatch("/api/**").build();
    }
}
