package com.example.analysis.entity.utility;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DashboardSummary {
    private String countrySummary;
    private String citySummary;
    private String regionSummary;
    private String asnSummary;
    private String threatTypeSummary;
    private String tagsSummary;
    private String reporterSummary;
    private String urlStatusSummary;
    private String spamhausSummary;
    private String surblSummary;
    private String malwareFamilySummary;
    private String confidenceSummary;
    private String threatCategorySummary;
}
