package com.github.mehdihadeli.buildingblocks.core.exceptions;

public class ValidationException extends BadRequestException {
    public ValidationException(String message, Throwable innerException, String... errors) {
        super(message, innerException, errors);
    }

    public ValidationException(String message, String... errors) {
        this(message, null, errors);
    }
}
