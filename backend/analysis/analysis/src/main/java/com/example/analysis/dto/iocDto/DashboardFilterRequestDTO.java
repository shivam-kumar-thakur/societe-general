package com.example.analysis.dto.iocDto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardFilterRequestDTO {
    private String country;
    private String city;
    private String region;
    private String malwareFamily;
}