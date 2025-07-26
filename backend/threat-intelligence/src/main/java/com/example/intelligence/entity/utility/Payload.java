package com.example.intelligence.entity.utility;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Payload {
    private String firstseen;
    private String filename;
    private String file_type;
    private String response_size;
    private String response_md5;
    private String response_sha256;
    private String urlhaus_download;
    private String signature;
    private Virustotal virustotal;
}
