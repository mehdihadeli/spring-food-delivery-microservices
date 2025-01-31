package com.github.mehdihadeli.buildingblocks.problemdetails;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class ProblemDetailsDefaults {

    private static final URI BLANK_TYPE = URI.create("about:blank");
    private static final Map<HttpStatus, ProblemTypeMap> ProblemTypeMaps = new EnumMap<>(HttpStatus.class);

    static {
        ProblemTypeMaps.put(
                HttpStatus.BAD_REQUEST,
                new ProblemTypeMap("Bad Request", "https://tools.ietf.org/html/rfc9110#section-15.5.1"));
        ProblemTypeMaps.put(
                HttpStatus.UNAUTHORIZED,
                new ProblemTypeMap("Unauthorized", "https://tools.ietf.org/html/rfc9110#section-15.5.2"));
        ProblemTypeMaps.put(
                HttpStatus.FORBIDDEN,
                new ProblemTypeMap("Forbidden", "https://tools.ietf.org/html/rfc9110#section-15.5.4"));
        ProblemTypeMaps.put(
                HttpStatus.NOT_FOUND,
                new ProblemTypeMap("Not Found", "https://tools.ietf.org/html/rfc9110#section-15.5.5"));
        ProblemTypeMaps.put(
                HttpStatus.METHOD_NOT_ALLOWED,
                new ProblemTypeMap("Method Not Allowed", "https://tools.ietf.org/html/rfc9110#section-15.5.6"));
        ProblemTypeMaps.put(
                HttpStatus.NOT_ACCEPTABLE,
                new ProblemTypeMap("Not Acceptable", "https://tools.ietf.org/html/rfc9110#section-15.5.7"));
        ProblemTypeMaps.put(
                HttpStatus.REQUEST_TIMEOUT,
                new ProblemTypeMap("Request Timeout", "https://tools.ietf.org/html/rfc9110#section-15.5.9"));
        ProblemTypeMaps.put(
                HttpStatus.CONFLICT,
                new ProblemTypeMap("Conflict", "https://tools.ietf.org/html/rfc9110#section-15.5.10"));
        ProblemTypeMaps.put(
                HttpStatus.PRECONDITION_FAILED,
                new ProblemTypeMap("Precondition Failed", "https://tools.ietf.org/html/rfc9110#section-15.5.13"));
        ProblemTypeMaps.put(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                new ProblemTypeMap("Unsupported Media Type", "https://tools.ietf.org/html/rfc9110#section-15.5.16"));
        ProblemTypeMaps.put(
                HttpStatus.UNPROCESSABLE_ENTITY,
                new ProblemTypeMap("Unprocessable Entity", "https://tools.ietf.org/html/rfc4918#section-11.2"));
        ProblemTypeMaps.put(
                HttpStatus.UPGRADE_REQUIRED,
                new ProblemTypeMap("Upgrade Required", "https://tools.ietf.org/html/rfc9110#section-15.5.22"));
        ProblemTypeMaps.put(
                HttpStatus.INTERNAL_SERVER_ERROR,
                new ProblemTypeMap(
                        "An error occurred while processing your request.",
                        "https://tools.ietf.org/html/rfc9110#section-15.6.1"));
        ProblemTypeMaps.put(
                HttpStatus.BAD_GATEWAY,
                new ProblemTypeMap("Bad Gateway", "https://tools.ietf.org/html/rfc9110#section-15.6.3"));
        ProblemTypeMaps.put(
                HttpStatus.SERVICE_UNAVAILABLE,
                new ProblemTypeMap("Service Unavailable", "https://tools.ietf.org/html/rfc9110#section-15.6.4"));
        ProblemTypeMaps.put(
                HttpStatus.GATEWAY_TIMEOUT,
                new ProblemTypeMap("Gateway Timeout", "https://tools.ietf.org/html/rfc9110#section-15.6.5"));
    }

    public static void applyDefaults(ProblemDetail problemDetails, HttpStatus statusCode) {
        ProblemTypeMap problemTypeMap = ProblemTypeMaps.get(statusCode);
        if (problemTypeMap != null) {
            if (problemDetails.getTitle() == null) {
                problemDetails.setTitle(problemTypeMap.title());
            }
            if (Objects.equals(problemDetails.getType().getPath(), BLANK_TYPE.getPath())) {
                try {
                    problemDetails.setType(new URI(null, null, problemTypeMap.type(), null));
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException("Invalid URI for problem type: " + problemTypeMap.type(), e);
                }
            }
        } else {
            if (problemDetails.getTitle() == null) {
                problemDetails.setTitle(statusCode.getReasonPhrase());
            }
        }
    }

    private record ProblemTypeMap(String title, String type) {}
}
