package com.example.analysis.entity;


import com.example.analysis.dto.IocEnrichment;
import com.example.analysis.entity.utility.Payload;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

// main entity to store all types of ioc
@Document("ioc")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ioc {

    // id will be urlhaus id for there ioc alerts
    @Id
    private String id;
    // we will add source here like urlhaus etc
    private String source;

    // for urlhaus its : urlhaus_reference
    private String reference;
    private String url;
    private String url_status;
    private String host;
    private String date_added;
    private String threat;

    // spamhaus_dbl, surbl
    private Map<String, String> blacklists;

    private String reporter;
    private boolean larted;
    // e.g., "32-bit", "elf", "mips", "Mozi"
    private List<String> tags;

    private List<Payload> payloads;

    private GeoInfo geoInfo;

    // now this will contain summary of our things from ai

    private IocEnrichment enrichmentData;

}
