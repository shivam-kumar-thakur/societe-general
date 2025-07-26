package com.example.analysis.utils;


import com.example.analysis.dto.iocDto.IocDashboardSummaryDTO;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EnrichmentPromptBuilder {

    public String buildPromptFromDashboardData(IocDashboardSummaryDTO dto) {
        StringBuilder sb = new StringBuilder();

        sb.append("You are a cybersecurity analyst. Given the following threat intelligence data counts, summarize each section in plain, human-understandable language. ")
                .append("Provide your response as a JSON object in the following format:\n\n")
                .append("{\n")
                .append("  \"countrySummary\": \"...\",\n")
                .append("  \"citySummary\": \"...\",\n")
                .append("  \"regionSummary\": \"...\",\n")
                .append("  \"asnSummary\": \"...\",\n")
                .append("  \"threatTypeSummary\": \"...\",\n")
                .append("  \"tagsSummary\": \"...\",\n")
                .append("  \"reporterSummary\": \"...\",\n")
                .append("  \"urlStatusSummary\": \"...\",\n")
                .append("  \"spamhausSummary\": \"...\",\n")
                .append("  \"surblSummary\": \"...\",\n")
                .append("  \"malwareFamilySummary\": \"...\",\n")
                .append("  \"confidenceSummary\": \"...\",\n")
                .append("  \"threatCategorySummary\": \"...\"\n")
                .append("}\n\n");

        sb.append("Now analyze and summarize these maps:\n\n");

        appendMap(sb, "Country Counts", dto.getCountryCounts());
        appendMap(sb, "City Counts", dto.getCityCounts());
        appendMap(sb, "Region Counts", dto.getRegionCounts());
        appendMap(sb, "ASN Counts", dto.getAsnCounts());
        appendMap(sb, "Threat Counts", dto.getThreatCounts());
        appendMap(sb, "Tag Counts", dto.getTagCounts());
        appendMap(sb, "Reporter Counts", dto.getReporterCounts());
        appendMap(sb, "URL Status Counts", dto.getUrlStatusCounts());
        appendMap(sb, "Spamhaus Counts", dto.getSpamhausCounts());
        appendMap(sb, "SURBL Counts", dto.getSurblCounts());
        appendMap(sb, "Malware Family Counts", dto.getMalwareFamilyCounts());
        appendMap(sb, "Confidence Score Counts", dto.getConfidenceScoreCounts());
        appendMap(sb, "Threat Category Counts", dto.getThreatCategoryCounts());

        return sb.toString();
    }

    private void appendMap(StringBuilder sb, String title, Map<String, Long> map) {
        sb.append("### ").append(title).append(":\n");
        if (map == null || map.isEmpty()) {
            sb.append("No data available.\n\n");
            return;
        }

        for (Map.Entry<String, Long> entry : map.entrySet()) {
            sb.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        sb.append("\n");
    }



}
