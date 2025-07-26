package com.example.intelligence.entity.utility;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Virustotal {
    private String result;
    private String percent;
    private String link;
}
