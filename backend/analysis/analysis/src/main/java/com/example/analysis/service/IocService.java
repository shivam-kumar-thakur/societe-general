package com.example.analysis.service;

import com.example.analysis.dto.ServiceResponseDTO;
import com.example.analysis.dto.iocDto.DashboardFilterRequestDTO;
import com.example.analysis.dto.iocDto.IocDashboardSummaryDTO;
import com.example.analysis.dto.iocDto.RecentIocRequestDTO;
import com.example.analysis.dto.iocDto.ShortDetailsDTO;
import com.example.analysis.entity.Ioc;
import com.example.analysis.entity.utility.DashboardSummary;
import com.example.analysis.repository.IocRepository;
import com.example.analysis.service.openSourceLLM.LlmClient;
import com.example.analysis.utils.EnrichmentPromptBuilder;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.bson.Document;


import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class IocService {

    private final IocRepository iocRepository;
    private final MongoTemplate mongoTemplate;

    private final LlmClient llmClient;
    private final EnrichmentPromptBuilder enrichmentPromptBuilder;
    public IocService(IocRepository iocRepository, MongoTemplate mongoTemplate, LlmClient llmClient, EnrichmentPromptBuilder enrichmentPromptBuilder) {
        this.iocRepository = iocRepository;
        this.mongoTemplate = mongoTemplate;
        this.llmClient = llmClient;
        this.enrichmentPromptBuilder = enrichmentPromptBuilder;
    }

    public ServiceResponseDTO findByFilters(RecentIocRequestDTO request) {
        try {
            Query query = new Query();

            if (request.getThreatType() != null) {
                query.addCriteria(Criteria.where("threat").is(request.getThreatType()));
            }

            if (request.getTags() != null && !request.getTags().isEmpty()) {
                query.addCriteria(Criteria.where("tags").all(request.getTags()));
            }

            if (request.getHasPayload() != null) {
                if (request.getHasPayload()) {
                    query.addCriteria(Criteria.where("payloads").not().size(0));
                } else {
                    query.addCriteria(Criteria.where("payloads").size(0));
                }
            }

            if (request.getCountry() != null) {
                query.addCriteria(Criteria.where("geoInfo.country").is(request.getCountry()));
            }

            if (request.getCity() != null) {
                query.addCriteria(Criteria.where("geoInfo.city").is(request.getCity()));
            }

            if (request.getRegion() != null) {
                query.addCriteria(Criteria.where("geoInfo.region").is(request.getRegion()));
            }

            if (request.getIp() != null) {
                query.addCriteria(Criteria.where("host").is(request.getIp()));
            }

            if (request.getAsn() != null) {
                query.addCriteria(Criteria.where("geoInfo.asn").is(request.getAsn()));
            }

            if (request.getOrg() != null) {
                query.addCriteria(Criteria.where("geoInfo.org").regex(request.getOrg(), "i"));
            }

            if (request.getInEu() != null) {
                query.addCriteria(Criteria.where("geoInfo.inEu").is(request.getInEu()));
            }

            if (request.getUrlStatus() != null) {
                query.addCriteria(Criteria.where("url_status").is(request.getUrlStatus()));
            }

            if (request.getFromDate() != null && request.getToDate() != null) {
                query.addCriteria(Criteria.where("date_added").gte(request.getFromDate()).lte(request.getToDate()));
            }

            if (request.getSpamhaus() != null) {
                query.addCriteria(Criteria.where("blacklists.spamhaus_dbl").is(request.getSpamhaus()));
            }

            if (request.getSurbl() != null) {
                query.addCriteria(Criteria.where("blacklists.surbl").is(request.getSurbl()));
            }

            if (request.getReporter() != null) {
                query.addCriteria(Criteria.where("reporter").is(request.getReporter()));
            }

            query.skip(request.getOffset()).limit(request.getLimit());

            List<Ioc> results = mongoTemplate.find(query, Ioc.class);
            List<ShortDetailsDTO> shortDetailsDTOS = new ArrayList<>();

            for (Ioc ioc : results) {
                shortDetailsDTOS.add(new ShortDetailsDTO(
                        ioc.getId(),
                        ioc.getEnrichmentData() != null ? ioc.getEnrichmentData().getSummary() : null,
                        ioc.getThreat(),
                        ioc.getHost(),
                        ioc.getUrl_status(),
                        ioc.getGeoInfo() != null ? ioc.getGeoInfo().getCountry() : null,
                        ioc.getTags(),
                        ioc.getEnrichmentData() != null ? ioc.getEnrichmentData().getMalwareFamily() : null,
                        ioc.getEnrichmentData() != null ? ioc.getEnrichmentData().getConfidenceScore() : null,
                        parseDate(ioc.getDate_added()),
                        ioc.getSource()
                ));
            }

            return new ServiceResponseDTO<>(true, "Filtered IOCs fetched successfully", HttpStatus.OK, shortDetailsDTOS);
        } catch (Exception e) {
            return new ServiceResponseDTO<>(false, "Error fetching IOCs: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    public ServiceResponseDTO getRecentIocDetails(String id) {
        try {
            Optional<Ioc> ioc = iocRepository.findById(id);
            if (ioc.isPresent()) {
                return new ServiceResponseDTO<>(true, "IOC found", HttpStatus.OK, ioc.get());
            } else {
                return new ServiceResponseDTO<>(false, "IOC not found with id: " + id, HttpStatus.NOT_FOUND, null);
            }
        } catch (Exception e) {
            return new ServiceResponseDTO<>(false, "Error retrieving IOC: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH);
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateStr, formatter);
            return zonedDateTime.toLocalDate();
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(dateStr.substring(0, 10));
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    public ServiceResponseDTO getIocDashboardSummaryFiltered(DashboardFilterRequestDTO dto) {
        try {
            List<AggregationOperation> pipeline = new ArrayList<>();

            // Apply filters if present
            List<Criteria> filters = new ArrayList<>();
            if (dto.getCountry() != null && !dto.getCountry().isEmpty()) {
                filters.add(Criteria.where("geoInfo.country").regex("^" + dto.getCountry() + "$", "i"));
            }
            if (dto.getCity() != null && !dto.getCity().isEmpty()) {
                filters.add(Criteria.where("geoInfo.city").regex("^" + dto.getCity() + "$", "i"));
            }
            if (dto.getRegion() != null && !dto.getRegion().isEmpty()) {
                filters.add(Criteria.where("geoInfo.region").regex("^" + dto.getRegion() + "$", "i"));
            }
            if (dto.getMalwareFamily() != null && !dto.getMalwareFamily().isEmpty()) {
                filters.add(Criteria.where("enrichmentData.malwareFamily").regex("^" + dto.getMalwareFamily() + "$", "i"));
            }

            if (!filters.isEmpty()) {
                pipeline.add(Aggregation.match(new Criteria().andOperator(filters.toArray(new Criteria[0]))));
            }

            FacetOperation facet = Aggregation.facet(getCountStages("geoInfo.country").toArray(new AggregationOperation[0]))
                    .as("countryCounts")
                    .and(getCountStages("geoInfo.city").toArray(new AggregationOperation[0])).as("cityCounts")
                    .and(getCountStages("geoInfo.region").toArray(new AggregationOperation[0])).as("regionCounts")
                    .and(getCountStages("geoInfo.asn").toArray(new AggregationOperation[0])).as("asnCounts")
                    .and(getCountStages("threat").toArray(new AggregationOperation[0])).as("threatCounts")
                    .and(getCountStages("reporter").toArray(new AggregationOperation[0])).as("reporterCounts")
                    .and(getCountStages("url_status").toArray(new AggregationOperation[0])).as("urlStatusCounts")
                    .and(getCountStages("blacklists.spamhaus_dbl").toArray(new AggregationOperation[0])).as("spamhausCounts")
                    .and(getCountStages("blacklists.surbl").toArray(new AggregationOperation[0])).as("surblCounts")
                    .and(getCountStages("enrichmentData.malwareFamily").toArray(new AggregationOperation[0])).as("malwareFamilyCounts")
                    .and(getCountStages("enrichmentData.confidenceScore").toArray(new AggregationOperation[0])).as("confidenceScoreCounts")
                    .and(getCountStages("enrichmentData.threatCategory").toArray(new AggregationOperation[0])).as("threatCategoryCounts")
                    .and(getArrayCountStages("tags").toArray(new AggregationOperation[0])).as("tagCounts");

            pipeline.add(facet);

            Aggregation aggregation = Aggregation.newAggregation(pipeline);
            AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "ioc", Document.class);
            Document resultDoc = results.getUniqueMappedResult();

            IocDashboardSummaryDTO summary = IocDashboardSummaryDTO.builder()
                    .countryCounts(toCountMap(resultDoc, "countryCounts"))
                    .cityCounts(toCountMap(resultDoc, "cityCounts"))
                    .regionCounts(toCountMap(resultDoc, "regionCounts"))
                    .asnCounts(toCountMap(resultDoc, "asnCounts"))
                    .threatCounts(toCountMap(resultDoc, "threatCounts"))
                    .reporterCounts(toCountMap(resultDoc, "reporterCounts"))
                    .urlStatusCounts(toCountMap(resultDoc, "urlStatusCounts"))
                    .spamhausCounts(toCountMap(resultDoc, "spamhausCounts"))
                    .surblCounts(toCountMap(resultDoc, "surblCounts"))
                    .malwareFamilyCounts(toCountMap(resultDoc, "malwareFamilyCounts"))
                    .confidenceScoreCounts(toCountMap(resultDoc, "confidenceScoreCounts"))
                    .threatCategoryCounts(toCountMap(resultDoc, "threatCategoryCounts"))
                    .tagCounts(toCountMap(resultDoc, "tagCounts"))
                    .build();

            // we will send this to ask for summary
            String prompt=enrichmentPromptBuilder.buildPromptFromDashboardData(summary);
            DashboardSummary dashboardSummary=llmClient.getDashboardEnrichmentGemini(prompt);

            summary.setDashboardSummary(dashboardSummary);

            return new ServiceResponseDTO<>(true, "Filtered IOC dashboard summary", HttpStatus.OK, summary);

        } catch (Exception e) {
            return new ServiceResponseDTO<>(false, "Error generating dashboard: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    private List<AggregationOperation> getCountStages(String field) {
        return List.of(
                Aggregation.match(Criteria.where(field).ne(null)),
                Aggregation.group(field).count().as("count"),
                Aggregation.project("count").and("_id").as("key")
        );
    }

    private List<AggregationOperation> getArrayCountStages(String arrayField) {
        return List.of(
                Aggregation.unwind(arrayField),
                Aggregation.match(Criteria.where(arrayField).ne(null)),
                Aggregation.group(arrayField).count().as("count"),
                Aggregation.project("count").and("_id").as("key")
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Long> toCountMap(Document doc, String key) {
        Map<String, Long> result = new HashMap<>();
        if (doc == null || doc.get(key) == null) return result;

        List<Document> list = (List<Document>) doc.get(key);
        for (Document item : list) {
            String k = item.getString("key");
            Object countObj = item.get("count");

            long count = 0L;
            if (countObj instanceof Integer) {
                count = ((Integer) countObj).longValue();
            } else if (countObj instanceof Long) {
                count = (Long) countObj;
            }
            result.put(k, count);
        }
        return result;
    }
}