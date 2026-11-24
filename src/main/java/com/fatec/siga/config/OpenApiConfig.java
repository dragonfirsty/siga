package com.fatec.siga.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI sigaOpenApi() {
		return new OpenAPI().info(new Info()
			.title("SIGA 2 - API")
			.description("API REST do controle acadêmico de chamadas")
			.version("1.0"));
	}
}
