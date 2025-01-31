package com.github.mehdihadeli.buildingblocks.core.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends CustomException {
    public BadRequestException(String message, Throwable innerException, String... errors) {
        super(message, HttpStatus.BAD_REQUEST, innerException, errors); // 400 is the HTTP Status Code for Bad Request
    }

    public BadRequestException(String message, String... errors) {
        this(message, null, errors);
    }
}
