package com.example.programare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.example.programare.feign"})
@ComponentScan(basePackages = {"com.example.common", "com.example.programare"})
public class ProgramareApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProgramareApplication.class, args);
	}

}
