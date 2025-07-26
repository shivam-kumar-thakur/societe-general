package com.example.analysis.entity;

import com.example.analysis.entity.utility.EnrichedVulnerability;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("vulnerability-report")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThreatReport {

    @Id
    private String cveNumber;
    private String source;

    private String reportId; // for talos its there id
    private String title;
    private LocalDate reportDate;

    private Double cvssScore;

    private String sourceUrl;
    // this will have info in better way
    private EnrichedVulnerability enrichedVulnerability;

}
