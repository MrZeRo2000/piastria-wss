package com.romanpulov.piastriawss.transform;

public class ExcelReadException extends Exception {
    public ExcelReadException(String message) {
        super("Error reading from Excel: " + message);
    }
}
