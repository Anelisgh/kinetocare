package com.example.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class  ApiGatewayApplication {

	@PostConstruct
	public void init() {
		// Hooks.enableAutomaticContextPropagation();
	}

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
