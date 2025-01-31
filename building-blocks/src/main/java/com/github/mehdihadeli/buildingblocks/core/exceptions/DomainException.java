package com.github.mehdihadeli.buildingblocks.core.exceptions;

import org.springframework.http.HttpStatus;

public class DomainException extends CustomException {
    public DomainException(String message) {
        super(message, HttpStatus.BAD_REQUEST, null); // Default HTTP Status Code is 400
    }
}
