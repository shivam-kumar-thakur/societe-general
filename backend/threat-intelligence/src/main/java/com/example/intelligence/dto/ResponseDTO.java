package com.example.intelligence.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {

    private boolean success;
    private String message;
    private T data;

}
