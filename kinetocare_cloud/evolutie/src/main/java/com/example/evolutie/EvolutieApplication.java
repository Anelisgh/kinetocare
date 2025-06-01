package com.example.evolutie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.example.evolutie.feign"})
@ComponentScan(basePackages = {"com.example.common", "com.example.evolutie"})
public class EvolutieApplication {
	public static void main(String[] args) {
		SpringApplication.run(EvolutieApplication.class, args);
	}

}
