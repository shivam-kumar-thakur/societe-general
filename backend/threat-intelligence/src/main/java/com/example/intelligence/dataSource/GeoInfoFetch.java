package com.example.intelligence.dataSource;

import com.example.intelligence.entity.GeoInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class GeoInfoFetch {

    private final RestTemplate restTemplate;

    public  GeoInfoFetch(RestTemplate restTemplate){
        this.restTemplate=restTemplate;
    }

    public GeoInfo fetchGeoInfo(String ip){
        String url = "https://ipapi.co/" + ip + "/json/";
        try {
            GeoInfo response = restTemplate.getForObject(url, GeoInfo.class);
            if (response != null) {
                log.info("Fetched GeoInfo for IP: {}", ip);
                log.info("Country {}",response.getCountry());
                // do here something like printing and all
                // if response null then also handel that condition
            }
            return response;
        } catch (RestClientException e) {
            log.error("Failed to fetch GeoInfo for IP: {}", ip, e);
            return null;
        }
    }
}
