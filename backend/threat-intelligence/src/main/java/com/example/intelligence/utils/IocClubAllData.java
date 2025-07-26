package com.example.intelligence.utils;

import com.example.intelligence.dto.rabbit.IocUrlHaus;
import com.example.intelligence.entity.GeoInfo;
import com.example.intelligence.entity.Ioc;
import com.example.intelligence.entity.utility.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IocClubAllData {

    public Ioc clubAllData(IocUrlHaus iocUrlHaus, List<Payload> payloads, GeoInfo geoInfo) {
        Ioc ioc = new Ioc();

        // Set basic IOC info from IocUrlHaus DTO
        ioc.setId(iocUrlHaus.getId());
        ioc.setReference(iocUrlHaus.getUrlhaus_reference()); // rename mapping
        ioc.setUrl(iocUrlHaus.getUrl());
        ioc.setUrl_status(iocUrlHaus.getUrl_status());
        ioc.setHost(iocUrlHaus.getHost());
        ioc.setDate_added(iocUrlHaus.getDate_added());
        ioc.setThreat(iocUrlHaus.getThreat());
        ioc.setBlacklists(iocUrlHaus.getBlacklists());
        ioc.setReporter(iocUrlHaus.getReporter());
        ioc.setLarted(iocUrlHaus.isLarted());
        ioc.setTags(iocUrlHaus.getTags());

        // extra values
        ioc.setSource("urlhaus");
        ioc.setPayloads(payloads);
        ioc.setGeoInfo(geoInfo);

        return ioc;
    }
}
