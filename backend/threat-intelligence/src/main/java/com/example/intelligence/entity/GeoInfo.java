package com.example.intelligence.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GeoInfo {
    private String ip;
    private String network;
    private String version;
    private String city;
    private String region;
    private String regionCode;
    private String country;
    private String countryName;
    private String countryCode;
    private String countryCodeIso3;
    private String countryCapital;
    private String countryTld;
    private String continentCode;
    private boolean inEu;
    private String postal;
    private double latitude;
    private double longitude;
    private String timezone;
    private String utcOffset;
    private String countryCallingCode;
    private String currency;
    private String currencyName;
    private String languages;
    private double countryArea;
    private long countryPopulation;
    private String asn;
    private String org;
}
