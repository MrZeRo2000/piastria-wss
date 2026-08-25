package com.romanpulov.piastriawss.dto;

public class ErrorResponseDTO {
    public final String message;
    public final String requestUrl;

    public ErrorResponseDTO(String message, String requestUrl) {
        this.message = message;
        this.requestUrl = requestUrl;
    }
}
