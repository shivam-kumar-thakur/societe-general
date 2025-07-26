package com.example.analysis.dto.iocDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShortDetailsDTO {

    private String id;
    private String summary;
    private String threat;
    private String host;
    private String urlStatus;
    private String country;
    private List<String> tags;
    private String malwareFamily;
    private String confidenceScore;
    private LocalDate dateAdded;
    private String source;
}
