package com.example.analysis.dto.iocDto;

import com.example.analysis.entity.utility.DashboardSummary;
import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IocDashboardSummaryDTO {

    private Map<String, Long> countryCounts;
    private Map<String, Long> cityCounts;
    private Map<String, Long> regionCounts;
    private Map<String, Long> asnCounts;
    private Map<String, Long> threatCounts;
    private Map<String, Long> tagCounts;
    private Map<String, Long> reporterCounts;
    private Map<String, Long> urlStatusCounts;
    private Map<String, Long> spamhausCounts;
    private Map<String, Long> surblCounts;

    // this will come from enrichment
    private Map<String, Long> malwareFamilyCounts;
    private Map<String, Long> confidenceScoreCounts;
    private Map<String, Long> threatCategoryCounts;


    // adding extra summary for easy understanding
    private DashboardSummary dashboardSummary;
}

