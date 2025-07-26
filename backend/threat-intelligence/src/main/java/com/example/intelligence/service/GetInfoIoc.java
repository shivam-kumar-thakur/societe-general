package com.example.intelligence.service;

import com.example.intelligence.dto.IocEnrichment;
import com.example.intelligence.entity.Ioc;
import com.example.intelligence.service.openSourceLLM.LlmClient;
import com.example.intelligence.utils.EnrichmentPromptBuilder;
import org.springframework.stereotype.Service;

@Service
public class GetInfoIoc {

    private final LlmClient llmClient;
    private final EnrichmentPromptBuilder enrichmentPromptBuilder;

    public GetInfoIoc(LlmClient llmClient, EnrichmentPromptBuilder enrichmentPromptBuilder) {
        this.llmClient = llmClient;
        this.enrichmentPromptBuilder = enrichmentPromptBuilder;
    }

    // here it will take and build a prompt an dthen call for mode
    public void enrichIoc(Ioc ioc){
        String prompt=enrichmentPromptBuilder.buildPromptFromIoc(ioc);

        // for now we will use gemeni api to test it
        //IocEnrichment enrichedIoc=llmClient.getEnrichmentMistral(prompt);
        IocEnrichment enrichedIoc=llmClient.getIocEnrichmentGemini(prompt);

        ioc.setEnrichmentData(enrichedIoc);

        return;
    }

}
