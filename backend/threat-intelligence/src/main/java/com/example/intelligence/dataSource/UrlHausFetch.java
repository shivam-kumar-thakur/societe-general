package com.example.intelligence.dataSource;

import com.example.intelligence.dto.rabbit.IocUrlHaus;
import com.example.intelligence.entity.utility.Payload;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.*;

@Service
@Slf4j
public class UrlHausFetch {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${apikey.urlhaus}")
    private String apiKey;

    public UrlHausFetch(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<IocUrlHaus> fetchRecent(int count){
        // it will limit the fetch till that count
        String url = "https://urlhaus-api.abuse.ch/v1/urls/recent/limit/"+count;

        HttpHeaders headers= new HttpHeaders();
        headers.set("Auth-Key",apiKey);

        HttpEntity<String> entity=new HttpEntity<>(headers);

        try{
            ResponseEntity<Map> response=restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            Map<String, Object> body=response.getBody();
            if(body!=null && body.containsKey("urls")){
                List<Map<String,Object>> urlList=(List<Map<String,Object>>) body.get("urls");

                return objectMapper.convertValue(urlList, new TypeReference<List<IocUrlHaus>>() {});
            }
        }catch (Exception e) {
            log.error("❌ Failed to fetch or parse URLHaus data", e);
        }

        return Collections.emptyList();

    }

    // we will fetch with id
    public List<Payload> fetchPayloadInfoWithId(String urlid) {
        String url = "https://urlhaus-api.abuse.ch/v1/urlid/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Auth-Key", apiKey);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("urlid", urlid);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(
                body, headers
        );

        try {

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            Map<String, Object> responseBody=response.getBody();
            if(responseBody!=null && responseBody.containsKey("payloads")){
                List<Map<String,Object>> payloadData=(List<Map<String,Object>>) responseBody.get("payloads");

                return objectMapper.convertValue(payloadData, new TypeReference<List<Payload>>() {});
            }


        } catch (Exception e) {
            log.error("❌ Failed to fetch URLHaus details by url_id: {}", urlid, e);
            return null;
        }
        return null;
    }

    // can add to fetch with url directly
    // can addto fetch with signature and sha but we are already grtting all info

}

