package com.github.mehdihadeli.buildingblocks.core.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends CustomException {
    public ConflictException(String message, Throwable innerException) {
        super(message, HttpStatus.CONFLICT, innerException); // 409 is the HTTP Status Code for Conflict
    }

    public ConflictException(String message) {
        this(message, null);
    }
}
