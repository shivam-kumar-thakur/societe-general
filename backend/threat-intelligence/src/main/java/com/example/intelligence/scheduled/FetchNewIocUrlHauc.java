package com.example.intelligence.scheduled;

import com.example.intelligence.dataSource.UrlHausFetch;
import com.example.intelligence.dto.rabbit.IocUrlHaus;
import com.example.intelligence.service.rabbitmq.RabbitMQProducer;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FetchNewIocUrlHauc {

    private final UrlHausFetch urlHausFetch;
    private final RabbitMQProducer rabbitMQProducer;

    public FetchNewIocUrlHauc(UrlHausFetch urlHausFetch, RabbitMQProducer rabbitMQProducer) {
        this.urlHausFetch = urlHausFetch;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @PostConstruct
    public void runOnStart(){
        sendIocToRabbit();
    }

    @Async
    @Scheduled(cron = "0 0 * * * *") // Every hour, on the hour
    public void sendIocToRabbit(){

        List<IocUrlHaus> newIoc=urlHausFetch.fetchRecent(10);
        for(IocUrlHaus ioc:newIoc){
            // send them for processing
            rabbitMQProducer.sendUrlHausIoc(ioc);
            System.out.println(ioc.getUrl());
        }

    }

}
