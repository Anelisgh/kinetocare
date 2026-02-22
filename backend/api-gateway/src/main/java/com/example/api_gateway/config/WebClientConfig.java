package com.example.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean("userWebClient")
    public WebClient userWebClient(@Value("${application.urls.user-service}") String url, WebClient.Builder builder) {
        return builder.baseUrl(url).build();
    }

    @Bean("pacientiWebClient")
    public WebClient pacientiWebClient(@Value("${application.urls.pacienti-service}") String url, WebClient.Builder builder) {
        return builder.baseUrl(url).build();
    }

    @Bean("terapeutiWebClient")
    public WebClient terapeutiWebClient(@Value("${application.urls.terapeuti-service}") String url, WebClient.Builder builder) {
        return builder.baseUrl(url).build();
    }

    @Bean("programariWebClient")
    public WebClient programariWebClient(@Value("${application.urls.programari-service}") String url, WebClient.Builder builder) {
        return builder.baseUrl(url).build();
    }

    @Bean("serviciiWebClient")
    public WebClient serviciiWebClient(@Value("${application.urls.servicii-service}") String url, WebClient.Builder builder) {
        return builder.baseUrl(url).build();
    }
}
