package com.team66.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI energiAiOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("EnergiAI API")
                        .description("API REST para el analisis inteligente del consumo energetico: "
                                + "clasifica perfiles de eficiencia y estima costos mensuales.")
                        .version("v1"));
    }
}
