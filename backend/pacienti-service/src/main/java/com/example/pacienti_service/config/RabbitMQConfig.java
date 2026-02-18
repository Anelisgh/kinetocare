package com.example.pacienti_service.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // trebuie sa stie doar exchange-ul, nu-l intereseaza daca exista cineva care sa citeasca mesajul sau in ce coada ajunge; doar il arunca in exchange
    public static final String EXCHANGE_NAME = "notificari.exchange";

    @Bean
    public TopicExchange notificariExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
