package com.example.intelligence.service;

import com.example.intelligence.dto.IocEnrichment;
import com.example.intelligence.entity.Ioc;
import com.example.intelligence.entity.ThreatReport;
import com.example.intelligence.entity.utility.EnrichedVulnerability;
import com.example.intelligence.service.openSourceLLM.LlmClient;
import com.example.intelligence.utils.EnrichmentPromptBuilder;
import org.springframework.stereotype.Service;

@Service
public class GetThreatInfo {
    private final LlmClient llmClient;
    private final EnrichmentPromptBuilder enrichmentPromptBuilder;

    public GetThreatInfo(LlmClient llmClient, EnrichmentPromptBuilder enrichmentPromptBuilder) {
        this.llmClient = llmClient;
        this.enrichmentPromptBuilder = enrichmentPromptBuilder;
    }

    // here it will take and build a prompt
    public void enrichVulnerability(ThreatReport threatReport,String rawReport){
        String prompt=enrichmentPromptBuilder.buildPromptFromThreat(threatReport,rawReport);

        // for now we will use gemeni api to test it
        //IocEnrichment enrichedIoc=llmClient.getEnrichmentMistral(prompt);
        EnrichedVulnerability enrichedIoc=llmClient.getVulnerabilityEnrichmentGemini(prompt);

        threatReport.setEnrichedVulnerability(enrichedIoc);

        return;
    }
}
