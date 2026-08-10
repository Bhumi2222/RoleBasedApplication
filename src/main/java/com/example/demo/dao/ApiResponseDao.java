package com.example.demo.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDao<T> {

    private Integer status;

    private String message;

    private T data;

    private String errorCode;

    private LocalDateTime timestamp;

    public static <T> ApiResponseDao<T> success(T data) {

        return ApiResponseDao.<T>builder()
                .status(200)
                .message("Success")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponseDao<T> success(
            String message,
            T data) {

        return ApiResponseDao.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponseDao<T> error(
            Integer status,
            String message,
            String errorCode) {

        return ApiResponseDao.<T>builder()
                .status(status)
                .message(message)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
    }
}