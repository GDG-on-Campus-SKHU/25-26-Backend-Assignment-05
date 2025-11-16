package com.gdg.todolist.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi todoApi() {
        return GroupedOpenApi.builder()
                .group("Todo API")
                .pathsToMatch("/todo/**")
                .build();
    }

    @Bean
    public GroupedOpenApi signupApi() {
        return GroupedOpenApi.builder()
                .group("Signup API")
                .pathsToMatch("/api/**")
                .build();
    }
}
