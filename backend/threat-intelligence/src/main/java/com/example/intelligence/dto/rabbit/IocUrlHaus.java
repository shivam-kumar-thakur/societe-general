package com.example.intelligence.dto.rabbit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IocUrlHaus {

    @Id
    private String id;
    private String urlhaus_reference;
    private String url;
    private String url_status;
    private String host;
    private String date_added;
    private String threat;

    private Map<String, String> blacklists; // spamhaus_dbl, surbl

    private String reporter;
    private boolean larted;
    private List<String> tags; // e.g., "32-bit", "elf", "mips", "Mozi"
}
