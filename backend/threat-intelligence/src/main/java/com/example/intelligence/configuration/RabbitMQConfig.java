package com.example.intelligence.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.data.rabbitmq.queue.ioc-collection-urlhaus}")
    private String iocUrlHaucQueue;

    @Value("${spring.data.rabbitmq.exchange.ioc-collection-urlhaus}")
    private String iocUrlHaucExchange;

    @Value("${spring.data.rabbitmq.routing.ioc-collection-urlhaus}")
    private String iocUrlHaucRoutingkey;

    @Value("${spring.data.rabbitmq.queue.vulnerability-collection-talos}")
    private String talosQueue;

    @Value("${spring.data.rabbitmq.exchange.vulnerability-collection-talos}")
    private String talosExchange;

    @Value("${spring.data.rabbitmq.routing.vulnerability-collection-talos}")
    private String talosRoutingkey;


    // --------------------For UrlHauc --------------------

    @Bean
    public Queue urlHaucQueue() {
        return new Queue(iocUrlHaucQueue, true);
    }

    @Bean
    public TopicExchange urlHaucTOPicExchange() {
        return new TopicExchange(iocUrlHaucExchange);
    }

    @Bean
    public Binding urlHaucBinding() {
        return BindingBuilder
                .bind(urlHaucQueue())
                .to(urlHaucTOPicExchange())
                .with(iocUrlHaucRoutingkey);
    }

    // --------------------For Talos --------------------

    @Bean
    public Queue talosQueue() {
        return new Queue(talosQueue, true);
    }

    @Bean
    public TopicExchange talosExchange() {
        return new TopicExchange(talosExchange);
    }

    @Bean
    public Binding talosBinding() {
        return BindingBuilder
                .bind(talosQueue())
                .to(talosExchange())
                .with(talosRoutingkey);
    }



    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    // we use a custom name to avoid conflict with Spring Boot's default RabbitTemplate
    @Bean(name = "iocRabbitTemplate")
    public AmqpTemplate customRabbitTemplateIoc(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    // we cna use same but for clarity letx make 2
    @Bean(name = "vulnerabilityRabbitTemplate")
    public AmqpTemplate customRabbitTemplateVulnerability(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

}
