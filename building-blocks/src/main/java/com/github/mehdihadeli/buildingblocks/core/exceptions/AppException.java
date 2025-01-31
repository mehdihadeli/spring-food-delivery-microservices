package com.github.mehdihadeli.buildingblocks.core.exceptions;

import org.springframework.http.HttpStatus;

public class AppException extends CustomException {
    public AppException(String message, HttpStatus statusCode, Throwable innerException) {
        super(message, statusCode, innerException);
    }

    public AppException(String message) {
        this(message, HttpStatus.BAD_REQUEST, null); // Default HTTP Status Code is 400
    }
}
