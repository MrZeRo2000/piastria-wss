package com.romanpulov.piastriawss.exception;

import com.romanpulov.piastriawss.exception.NotFoundException;

public class CommonEntityNotFoundException extends NotFoundException {
    public CommonEntityNotFoundException(Long id) {
        super(String.format("Entity with id=%d not found", id));
    }
}
