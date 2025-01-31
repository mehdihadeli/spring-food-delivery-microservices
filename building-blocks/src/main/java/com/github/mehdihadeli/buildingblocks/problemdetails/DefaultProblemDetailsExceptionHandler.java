package com.github.mehdihadeli.buildingblocks.problemdetails;

import com.github.mehdihadeli.buildingblocks.abstractions.problemdetails.IProblemDetailMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

public class DefaultProblemDetailsExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultProblemDetailsExceptionHandler.class);
    private final List<IProblemDetailMapper> problemDetailMappers;
    private final ProblemDetailsProperties problemDetailsProperties;

    public DefaultProblemDetailsExceptionHandler(
            List<IProblemDetailMapper> problemDetailMappers, ProblemDetailsProperties problemDetailsProperties) {
        this.problemDetailMappers = problemDetailMappers;
        this.problemDetailsProperties = problemDetailsProperties;
    }

    public ResponseEntity<ProblemDetail> handleException(
            HttpServletRequest request, HttpServletResponse response, Exception exception) {
        logger.error("An unexpected error occurred", exception);
        HttpStatus responseStatusCode = HttpStatus.INTERNAL_SERVER_ERROR;

        if (problemDetailMappers != null && !problemDetailMappers.isEmpty()) {
            for (IProblemDetailMapper mapper : problemDetailMappers) {
                var statusCode = mapper.getMappedStatusCodes(exception);
                if (statusCode != null) {
                    responseStatusCode = statusCode;
                    break; // Stop if a valid status code is found
                }
            }
        } else {
            var statusCode = new DefaultProblemDetailMapper().getMappedStatusCodes(exception);
            if (statusCode != null) {
                responseStatusCode = statusCode;
            }
        }

        ProblemDetail problemDetails = populateNewProblemDetail(responseStatusCode, request, exception);

        // Add stack trace if enabled in properties
        if (problemDetailsProperties.isIncludeStackTrace()) {
            problemDetails.setProperty("stackTrace", getStackTrace(exception));
        }

        ProblemDetailsDefaults.applyDefaults(problemDetails, responseStatusCode);
        response.setStatus(responseStatusCode.value());

        return ResponseEntity.status(responseStatusCode).body(problemDetails);
    }

    private ProblemDetail populateNewProblemDetail(
            HttpStatus httpStatus, HttpServletRequest request, Exception exception) {
        ProblemDetail problem = ProblemDetail.forStatus(httpStatus);
        problem.setDetail(exception.getMessage());
        problem.setTitle(exception.getClass().getSimpleName());

        try {
            String scheme = request.getScheme();
            String host = request.getServerName();
            int port = request.getServerPort();
            String requestUri = request.getRequestURI();
            URI uri = new URI(scheme, null, host, port, requestUri, null, null);

            problem.setInstance(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI for problem instance", e);
        }

        return problem;
    }

    // Extracted method for getting stack trace as string
    private static String getStackTrace(Exception exception) {
        return Arrays.stream(exception.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }
}
