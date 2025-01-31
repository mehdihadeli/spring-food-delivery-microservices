package com.github.mehdihadeli.buildingblocks.core.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomException {
    public NotFoundException(String message, Throwable innerException) {
        super(message, HttpStatus.NOT_FOUND, innerException); // 404 is the HTTP Status Code for Not Found
    }

    public NotFoundException(String message) {
        this(message, null);
    }
}
