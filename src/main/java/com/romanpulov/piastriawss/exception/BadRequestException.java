package com.romanpulov.piastriawss.exception;

public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(String.format("Bad request: %s", message));
    }
}
