package com.example.intelligence.service.rabbitmq;


import com.example.intelligence.dataSource.GeoInfoFetch;
import com.example.intelligence.dataSource.TalosIntelligence;
import com.example.intelligence.dataSource.UrlHausFetch;
import com.example.intelligence.dto.rabbit.IocUrlHaus;
import com.example.intelligence.entity.GeoInfo;
import com.example.intelligence.entity.Ioc;
import com.example.intelligence.entity.ThreatReport;
import com.example.intelligence.entity.utility.Payload;
import com.example.intelligence.repository.IocRepository;
import com.example.intelligence.repository.ThreatRepository;
import com.example.intelligence.service.GetInfoIoc;
import com.example.intelligence.service.GetThreatInfo;
import com.example.intelligence.utils.IocClubAllData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class RabbitConsumer {

    private final RabbitMQProducer rabbitMQProducer;
    private final TalosIntelligence talosIntelligence;
    private final UrlHausFetch urlHausFetch;
    private final GeoInfoFetch geoInfoFetch;
    private final IocClubAllData iocClubAllData;
    private final GetInfoIoc getInfoIoc;
    private final IocRepository iocRepository;
    private final GetThreatInfo getThreatInfo;
    private final ThreatRepository threatRepository;

    public RabbitConsumer(RabbitMQProducer rabbitMQProducer, TalosIntelligence talosIntelligence, UrlHausFetch urlHausFetch, GeoInfoFetch geoInfoFetch, IocClubAllData iocClubAllData, GetInfoIoc getInfoIoc, IocRepository iocRepository, GetThreatInfo getThreatInfo, ThreatRepository threatRepository) {
        this.rabbitMQProducer = rabbitMQProducer;
        this.talosIntelligence = talosIntelligence;
        this.urlHausFetch = urlHausFetch;
        this.geoInfoFetch = geoInfoFetch;
        this.iocClubAllData = iocClubAllData;
        this.getInfoIoc = getInfoIoc;
        this.iocRepository = iocRepository;
        this.getThreatInfo = getThreatInfo;
        this.threatRepository = threatRepository;
    }

    @RabbitListener(queues = "${spring.data.rabbitmq.queue.ioc-collection-urlhaus}")
    @Transactional
    public void processIocUrlHaus(IocUrlHaus iocUrlHaus){

        // we will take payload and ip data also
        String urlid=iocUrlHaus.getId();
        List<Payload> payload=urlHausFetch.fetchPayloadInfoWithId(urlid);
        GeoInfo geoInfo=geoInfoFetch.fetchGeoInfo(iocUrlHaus.getHost());

        // then we will fill schema
        Ioc newIoc=iocClubAllData.clubAllData(iocUrlHaus,payload,geoInfo);

        // then we will give this schema to ai models
        // it will auto add all other data in it
        getInfoIoc.enrichIoc(newIoc);

        System.out.println("Processign over in rabbit mq " + newIoc.getUrl());

        try{
            // and we will keeep things in our database
            iocRepository.save(newIoc);
        }catch (Exception e){
            log.error("Error in sending newioc",e);
        }

        System.out.println("Final send to db in rabbit mq " + newIoc.getUrl());
    }



    @RabbitListener(queues = "${spring.data.rabbitmq.queue.vulnerability-collection-talos}")
    @Transactional
    public void processTalosVulnerability(ThreatReport threatReport){
        String rawReport=talosIntelligence.talosFullVulnerabilityData(threatReport.getSourceUrl());
        System.out.println("Raw ------- "+rawReport);
        if(rawReport==null){
            // means no data skip it
            return;
        }
        getThreatInfo.enrichVulnerability(threatReport,rawReport);

        System.out.println("Processign over in rabbit mq " + threatReport.getSourceUrl());

        try{
            // and we will keeep things in our database
            threatRepository.save(threatReport);
        }catch (Exception e){
            log.error("Error in sending newioc",e);
        }

        System.out.println("Final send to db in rabbit mq " + threatReport.getSourceUrl());
    }

}
