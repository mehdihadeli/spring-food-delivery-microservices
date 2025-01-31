package com.github.mehdihadeli.buildingblocks.core.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public class CustomException extends RuntimeException {
    private final List<String> errorMessages;
    private final HttpStatus statusCode;

    public CustomException(String message, HttpStatus statusCode, Throwable innerException, String... errors) {
        // add stacktrace as well
        super(message, innerException, false, true);
        this.errorMessages = Arrays.asList(errors);
        this.statusCode = statusCode;
    }

    public CustomException(String message, int statusCode, Throwable innerException, String... errors) {
        // we can use `HttpStatus.resolve` or `HttpStatus.valueOf()` for getting `HttpStatus` from `int`
        this(message, HttpStatus.valueOf(statusCode), innerException, errors);
    }

    public CustomException(String message, String... errors) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR, null, errors);
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
