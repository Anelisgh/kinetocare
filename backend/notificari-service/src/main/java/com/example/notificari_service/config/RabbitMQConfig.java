package com.example.notificari_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // exchange -> oficiul de sortare;
    // primeste mesajul, se uita la eticheta mesajului (routing key) si decide unde sa il puna
    public static final String EXCHANGE_NAME = "notificari.exchange";
    // queue -> cutia unde se stocheaza mesajele
    // sunt stochate mesajele pana cand cineva vine sa le ia; daca serviciul este oprit mesajele se aduna aici, nu se pierd (pt ca durable=true)
    public static final String QUEUE_NAME = "notificari.queue";
    // binding -> legatura dintre exchange si queue
    // ii spune exchange-ului in ce coada sa trimita mesajul
    // # = orice urmeaza dupa (deci prinde si notificare.reevaluare si notificare.reevaluare.recomandata)
    public static final String ROUTING_KEY_PATTERN = "notificare.#";

    @Bean
    public TopicExchange notificariExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue notificariQueue() {
        return new Queue(QUEUE_NAME, true); // durable = true
    }

    // deci routing key va arata ca notificare.reevaluare.recomandata (BindingBuilder.bind(queue).to(exchange).with("notificare.#"))
    @Bean
    public Binding binding(Queue notificariQueue, TopicExchange notificariExchange) {
        return BindingBuilder.bind(notificariQueue)
                .to(notificariExchange)
                .with(ROUTING_KEY_PATTERN);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
