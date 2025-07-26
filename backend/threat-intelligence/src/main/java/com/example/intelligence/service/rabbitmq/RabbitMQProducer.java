package com.example.intelligence.service.rabbitmq;

import com.example.intelligence.dto.rabbit.IocUrlHaus;
import com.example.intelligence.entity.ThreatReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



@Service
@Slf4j
public class RabbitMQProducer {

    private final AmqpTemplate rabbitTemplateIoc;
    private final AmqpTemplate rabbitTemplateVulnerability;

    private final String exchangeUrlHaus;
    private final String routingKeyUrlHaus;

    private final String exchangeTalos;
    private final String routingKeyTalos;


    public RabbitMQProducer(@Qualifier("iocRabbitTemplate") AmqpTemplate rabbitTemplateIoc,
                            @Qualifier("vulnerabilityRabbitTemplate") AmqpTemplate rabbitTemplateVulnerability,
                            @Value("${spring.data.rabbitmq.exchange.ioc-collection-urlhaus}") String urlHausExchange,
                            @Value("${spring.data.rabbitmq.routing.ioc-collection-urlhaus}") String urlHausRoutingKey,
                            @Value("${spring.data.rabbitmq.exchange.vulnerability-collection-talos}") String talosExchange,
                            @Value("${spring.data.rabbitmq.routing.vulnerability-collection-talos}") String talosRoutingKey
                         ) {
        this.rabbitTemplateIoc = rabbitTemplateIoc;
        this.exchangeUrlHaus=urlHausExchange;
        this.routingKeyUrlHaus=urlHausRoutingKey;
        this.exchangeTalos=talosExchange;
        this.routingKeyTalos=talosRoutingKey;
        this.rabbitTemplateVulnerability=rabbitTemplateVulnerability;
    }

    public void sendUrlHausIoc(IocUrlHaus iocUrlHaus){
        rabbitTemplateIoc.convertAndSend(exchangeUrlHaus,routingKeyUrlHaus,iocUrlHaus);
        log.info("Sent to RabbitMQ: {} ",iocUrlHaus.getId());
    }

    public void sendTalosVulnerability(ThreatReport threatReport){
        rabbitTemplateVulnerability.convertAndSend(exchangeTalos,routingKeyTalos,threatReport);
        log.info("Sent to RabbitMQ: {} ",threatReport.getReportId());
    }

}
