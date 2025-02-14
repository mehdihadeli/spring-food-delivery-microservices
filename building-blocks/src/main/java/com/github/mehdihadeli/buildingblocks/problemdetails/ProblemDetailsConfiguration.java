package com.github.mehdihadeli.buildingblocks.problemdetails;

import com.github.mehdihadeli.buildingblocks.abstractions.problemdetails.IProblemDetailMapper;
import com.github.mehdihadeli.buildingblocks.core.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

// when `proxyBeanMethods = false`, avoids the direct method call problem that would occur when one @Bean method calls
// another internally.
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@EnableConfigurationProperties(ProblemDetailsProperties.class)
@ConditionalOnClass({DefaultProblemDetailsExceptionHandler.class})
@ConditionalOnProperty(prefix = "problem-details", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ProblemDetailsConfiguration {

    @Bean
    @Scope("singleton")
    @ConditionalOnMissingBean
    public DefaultProblemDetailsExceptionHandler defaultProblemDetailsExceptionHandler(
            List<IProblemDetailMapper> mappers, ProblemDetailsProperties properties) {
        return new DefaultProblemDetailsExceptionHandler(mappers, properties);
    }

    @Bean
    @Scope("singleton")
    @ConditionalOnMissingBean
    public List<IProblemDetailMapper> problemDetailMappers() {
        return List.of(new DefaultProblemDetailMapper());
    }

    // We can import `nested configuration classes` that are annotated with @Configuration or @Component because
    // actually it is possible we do @Import for classes with @Configuration or @Component attribute, it is also
    // possible for nested classed, and they imported with their parent @Configuration
    @RestControllerAdvice
    public static class GlobalExceptionHandler {
        private final DefaultProblemDetailsExceptionHandler exceptionHandler;

        public GlobalExceptionHandler(DefaultProblemDetailsExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
        }

        @ExceptionHandler({
            ConflictException.class,
            ValidationException.class,
            BadRequestException.class,
            NotFoundException.class,
            HttpResponseException.class,
            AppException.class,
            CustomException.class,
            AuthenticationException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpMediaTypeNotAcceptableException.class,
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class,
            ServletRequestBindingException.class,
            MethodArgumentNotValidException.class,
            HandlerMethodValidationException.class,
            NoHandlerFoundException.class,
            NoResourceFoundException.class,
            AsyncRequestTimeoutException.class,
            ErrorResponseException.class,
            MaxUploadSizeExceededException.class,
            ConversionNotSupportedException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            MethodValidationException.class,
            BindException.class,
            AsyncRequestNotUsableException.class,
            InsufficientAuthenticationException.class,
            BadCredentialsException.class,
            AccessDeniedException.class,
            AuthenticationException.class,
            RuntimeException.class
        })
        @Nullable
        public ResponseEntity<ProblemDetail> handleException(
                HttpServletRequest request, HttpServletResponse response, Exception exception) {
            return handleExceptions(exceptionHandler, request, response, exception);
        }

        @Nullable
        public static ResponseEntity<ProblemDetail> handleExceptions(
                DefaultProblemDetailsExceptionHandler exceptionHandler,
                HttpServletRequest request,
                HttpServletResponse response,
                Exception exception) {
            return exceptionHandler.handleException(request, response, exception);
        }
    }
}
