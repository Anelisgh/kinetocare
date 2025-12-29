package com.example.programari_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients
@SpringBootApplication
public class ProgramariServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProgramariServiceApplication.class, args);
	}

}
