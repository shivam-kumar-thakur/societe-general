package com.example.analysis.service.openSourceLLM;

import com.example.analysis.dto.IocEnrichment;
import com.example.analysis.entity.utility.DashboardSummary;
import com.example.analysis.entity.utility.EnrichedVulnerability;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LlmClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${llm.ollama-url}")
    private String ollamaUrl;

    @Value("${llm.gemini-api-url}")
    private String geminiApiUrl;

    @Value("${llm.gemini-api-key}")
    private String geminiApiKey;

    public LlmClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // ----------------- for enrichment of ioc -------------------------
    public IocEnrichment getIocEnrichmentMistral(String prompt) {
        return getIocEnrichmentFromOllama(prompt, "mistral");
    }

    public IocEnrichment getIocEnrichmentLlama(String prompt) {
        return getIocEnrichmentFromOllama(prompt, "llama2");
    }

    public IocEnrichment getIocEnrichmentFromOllama(String prompt, String modelName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", modelName,
                "prompt", prompt,
                "stream", false
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    ollamaUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            String rawOutput = (String) response.getBody().get("response");

            return objectMapper.readValue(rawOutput, IocEnrichment.class);

        } catch (Exception e) {
            log.error(" Failed to get enrichment from Ollama model: {}", modelName, e);
            return null;
        }
    }

    public IocEnrichment getIocEnrichmentGemini(String prompt) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("x-goog-api-key", geminiApiKey);

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                ))
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    geminiApiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                String output = (String) parts.get(0).get("text");

                // Debug raw LLM response
                System.out.println("🔍 Gemini raw output:\n" + output);

                // Clean triple backticks and markdown
                if (output.startsWith("```")) {
                    output = output.replaceAll("(?s)```(json)?", "").replaceAll("```", "").trim();
                }

                return objectMapper.readValue(output, IocEnrichment.class);
            } else {
                log.warn("No valid Gemini response found");
                return null;
            }

        } catch (Exception e) {
            log.error("Failed to get enrichment from Gemini", e);
            return null;
        }
    }



    // ----------------- for enrichment of Dashboard -------------------------

    public DashboardSummary getDashboardEnrichmentMistral(String prompt) {
        return getDashboardEnrichmentFromOllama(prompt, "mistral");
    }

    public DashboardSummary getDashboardEnrichmentLlama(String prompt) {
        return getDashboardEnrichmentFromOllama(prompt, "llama2");
    }

    public DashboardSummary getDashboardEnrichmentFromOllama(String prompt, String modelName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", modelName,
                "prompt", prompt,
                "stream", false
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    ollamaUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            String rawOutput = (String) response.getBody().get("response");

            return objectMapper.readValue(rawOutput, DashboardSummary.class);

        } catch (Exception e) {
            log.error(" Failed to get enrichment from Ollama model: {}", modelName, e);
            return null;
        }
    }

    public DashboardSummary getDashboardEnrichmentGemini(String prompt) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("x-goog-api-key", geminiApiKey);

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                ))
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    geminiApiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                String output = (String) parts.get(0).get("text");

                // Debug raw LLM response
                System.out.println("🔍 Gemini raw output:\n" + output);

                // Clean triple backticks and markdown
                if (output.startsWith("```")) {
                    output = output.replaceAll("(?s)```(json)?", "").replaceAll("```", "").trim();
                }

                return objectMapper.readValue(output, DashboardSummary.class);
            } else {
                log.warn("No valid Gemini response found");
                return null;
            }

        } catch (Exception e) {
            log.error("Failed to get enrichment from Gemini", e);
            return null;
        }
    }




    // ----------------- for enrichment of vulnerability -------------------------
    public EnrichedVulnerability getVulnerabilityEnrichmentMistral(String prompt) {
        return getVulnerabilityEnrichmentFromOllama(prompt, "mistral");
    }

    public EnrichedVulnerability getVulnerabilityEnrichmentLlama(String prompt) {
        return getVulnerabilityEnrichmentFromOllama(prompt, "llama2");
    }


    public EnrichedVulnerability getVulnerabilityEnrichmentFromOllama(String prompt, String modelName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", modelName,
                "prompt", prompt,
                "stream", false
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    ollamaUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            String rawOutput = (String) response.getBody().get("response");

            return objectMapper.readValue(rawOutput, EnrichedVulnerability.class);

        } catch (Exception e) {
            log.error(" Failed to get enrichment from Ollama model: {}", modelName, e);
            return null;
        }
    }

    public EnrichedVulnerability getVulnerabilityEnrichmentGemini(String prompt) {
        System.out.println(prompt);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("x-goog-api-key", geminiApiKey);

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                ))
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    geminiApiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                String output = (String) parts.get(0).get("text");

                System.out.println("🔍 Gemini raw output:\n" + output);

                // Clean triple backticks and markdown
                if (output.startsWith("```")) {
                    output = output.replaceAll("(?s)```(json)?", "").replaceAll("```", "").trim();
                }

                //  Sanitize JSON before parsing : it was giving point error
                output = sanitizeJson(output);

                return objectMapper.readValue(output, EnrichedVulnerability.class);
            } else {
                log.warn("No valid Gemini response found");
                return null;
            }

        } catch (Exception e) {
            log.error("Failed to get enrichment from Gemini", e);
            return null;
        }
    }


    private String sanitizeJson(String rawJson) {
        // Fix decimal points without trailing digits (e.g. "8." -> "8.0")
        return rawJson.replaceAll("(\\d+)\\.(\\s|,|})", "$1.0$2");
    }
}
