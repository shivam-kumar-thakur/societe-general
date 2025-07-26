package com.example.analysis.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IocEnrichment {
    private String summary;
    private List<String> recommendActions;
    private List<String> threatDevices;

    // fields to support extended enrichment
    private String malwareFamily;
    private String confidenceScore;
    private String threatCategory;
    private List<String> targetSectors;
    private String persistenceMechanism;
    private List<String> exploitVectors;
    private List<String> relatedCVE;
}
