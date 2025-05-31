package com.example.evaluare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.example.evaluare.feign"})
@ComponentScan(basePackages = {"com.example.common", "com.example.evaluare"})
public class EvaluareApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvaluareApplication.class, args);
	}

}
