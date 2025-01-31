package com.github.mehdihadeli.buildingblocks.core.exceptions;

import java.util.Collections;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class HttpResponseException extends CustomException {
    private final String responseContent;
    private final Map<String, String[]> headers;

    public HttpResponseException(
            HttpStatus statusCode, String responseContent, Map<String, String[]> headers, Throwable innerException) {
        super(responseContent, statusCode, innerException);
        this.responseContent = responseContent;
        this.headers = headers != null ? headers : Collections.emptyMap();
    }

    public HttpResponseException(HttpStatus statusCode, String responseContent) {
        this(statusCode, responseContent, null, null);
    }

    public String getResponseContent() {
        return responseContent;
    }

    public Map<String, String[]> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "HTTP Response: \n\n" + responseContent + "\n\n" + super.toString();
    }
}
