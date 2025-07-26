package com.example.analysis.dto.iocDto;


import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RecentIocRequestDTO {
    // Threat Filters
    private String threatType;
    private List<String> tags;
    private Boolean hasPayload;

    // Geo Filters
    private String country;
    private String city;
    private String region;

    // Network Filters
    private String ip;
    private String asn;
    private String org;
    private Boolean inEu;

    // Status Filters
    private String urlStatus; // "online" or "offline"
    private LocalDate fromDate;
    private LocalDate toDate;

    // Blacklist Filters
    private String spamhaus;
    private String surbl;

    // Reporter Filter
    private String reporter;

    // Pagination
    private int offset = 0;
    private int limit = 20;
}