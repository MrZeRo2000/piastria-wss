package com.romanpulov.piastriawss.exception;

public class BadPatchRequestException extends Exception {
    public BadPatchRequestException(String patchRequestSection, String patchRequestValue) {
        super(String.format("Bad patch request: %s: %s", patchRequestSection, patchRequestValue));
    }
}
