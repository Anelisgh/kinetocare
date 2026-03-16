package com.example.notificari_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // trebuie sa stie doar exchange-ul, nu-l intereseaza daca exista cineva care sa citeasca mesajul sau in ce coada ajunge
    public static final String EXCHANGE_NAME = "notificari.exchange";
    
    // REDENUMITĂ: "notificari.queue.v2" pentru a evita eroarea PRECONDITION_FAILED.
    // O coadă existentă nu își poate schimba argumentele (am adăugat DLQ), așa că e nevoie de nume nou.
    public static final String QUEUE_NAME = "notificari.queue.v2";
    
    // binding -> legatura dintre exchange si queue
    // ii spune exchange-ului in ce coada sa trimita mesajul
    // # = orice urmeaza dupa (deci prinde si notificare.reevaluare si notificare.reevaluare.recomandata)
    public static final String ROUTING_KEY_PATTERN = "notificare.#";

    // DLQ — Dead Letter Queue pentru mesaje care nu pot fi procesate
    // Mesajele eșuate nu se pierd, ci merg în notificari.queue.dead pentru inspecție/replay manual
    public static final String DLX_EXCHANGE_NAME = "notificari.dlx";
    public static final String DLQ_NAME = "notificari.queue.dead";

    @Bean
    public TopicExchange notificariExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // Dead Letter Exchange — fanout (nu avem nevoie de routing key pentru DLQ)
    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(DLX_EXCHANGE_NAME);
    }

    // Coada principală — dacă procesarea eșuează, mesajul merge în DLX
    @Bean
    public Queue notificariQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME)
                .build();
    }

    // Dead Letter Queue — stochează mesajele eșuate pentru inspecție/replay manual
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_NAME).build();
    }

    // deci routing key va arata ca notificare.reevaluare.recomandata (BindingBuilder.bind(queue).to(exchange).with("notificare.#"))
    @Bean
    public Binding binding(Queue notificariQueue, TopicExchange notificariExchange) {
        return BindingBuilder.bind(notificariQueue)
                .to(notificariExchange)
                .with(ROUTING_KEY_PATTERN);
    }

    // Binding DLQ la DLX
    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, FanoutExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
