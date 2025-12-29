package com.example.pacienti_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PacientiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacientiServiceApplication.class, args);
	}

}
