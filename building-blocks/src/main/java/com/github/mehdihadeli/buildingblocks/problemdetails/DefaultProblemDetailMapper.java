package com.github.mehdihadeli.buildingblocks.problemdetails;

import com.github.mehdihadeli.buildingblocks.abstractions.problemdetails.IProblemDetailMapper;
import com.github.mehdihadeli.buildingblocks.core.exceptions.*;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

public class DefaultProblemDetailMapper implements IProblemDetailMapper {
    @Override
    public @Nullable HttpStatus getMappedStatusCodes(Exception exception) {
        return switch (exception) {
                // Custom exceptions
            case ConflictException conflictException -> conflictException.getStatusCode();
            case ValidationException validationException -> validationException.getStatusCode();
            case BadRequestException badRequestException -> badRequestException.getStatusCode();
            case NotFoundException notFoundException -> notFoundException.getStatusCode();
            case HttpResponseException httpResponseException -> httpResponseException.getStatusCode();
            case AppException appException -> appException.getStatusCode();

                // HTTP Method exceptions
            case HttpRequestMethodNotSupportedException e -> HttpStatus.METHOD_NOT_ALLOWED;

                // Media type exceptions
            case HttpMediaTypeNotSupportedException e -> HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            case HttpMediaTypeNotAcceptableException e -> HttpStatus.NOT_ACCEPTABLE;

                // Request parameter and binding exceptions
            case MissingPathVariableException e -> HttpStatus.BAD_REQUEST;
            case MissingServletRequestParameterException e -> HttpStatus.BAD_REQUEST;
            case MissingServletRequestPartException e -> HttpStatus.BAD_REQUEST;
            case ServletRequestBindingException e -> HttpStatus.BAD_REQUEST;

                // Validation exceptions
            case IllegalArgumentException e -> HttpStatus.BAD_REQUEST;
            case MethodArgumentNotValidException e -> HttpStatus.BAD_REQUEST;
            case HandlerMethodValidationException e -> HttpStatus.BAD_REQUEST;
            case MethodValidationException e -> HttpStatus.BAD_REQUEST;
            case BindException e -> HttpStatus.BAD_REQUEST;

                // Resource and handler exceptions
            case NoHandlerFoundException e -> HttpStatus.NOT_FOUND;
            case NoResourceFoundException e -> HttpStatus.NOT_FOUND;

                // Async request exceptions
            case AsyncRequestTimeoutException e -> HttpStatus.SERVICE_UNAVAILABLE;
            case AsyncRequestNotUsableException e -> HttpStatus.SERVICE_UNAVAILABLE;

                // Error response exceptions
            case ErrorResponseException e -> HttpStatus.INTERNAL_SERVER_ERROR;

                // File upload exceptions
            case MaxUploadSizeExceededException e -> HttpStatus.PAYLOAD_TOO_LARGE;

                // Type conversion and message conversion exceptions
            case ConversionNotSupportedException e -> HttpStatus.INTERNAL_SERVER_ERROR;
            case TypeMismatchException e -> HttpStatus.BAD_REQUEST;
            case HttpMessageNotReadableException e -> HttpStatus.BAD_REQUEST;
            case HttpMessageNotWritableException e -> HttpStatus.INTERNAL_SERVER_ERROR;

                // Default case
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
