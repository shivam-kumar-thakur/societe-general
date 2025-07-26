package com.example.intelligence.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponseDTO<T> {
    private boolean statusFlag;
    private String message;
    private HttpStatus statusCode;
    private T data;
}
