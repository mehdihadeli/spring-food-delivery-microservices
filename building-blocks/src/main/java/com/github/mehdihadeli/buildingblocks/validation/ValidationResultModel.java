package com.github.mehdihadeli.buildingblocks.validation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;

public class ValidationResultModel<TRequest> {
    private String message;
    private List<ValidationError> errors;

    public ValidationResultModel(ValidationErrors validationErrors, TRequest request) {
        String typeName = request.getClass().getSimpleName();

        String validationError = "Validation failed for type " + typeName;
        if (validationErrors != null && validationErrors.getErrors() != null) {
            errors = validationErrors.getErrors().stream()
                    .map(error -> new ValidationError(error.field(), error.errorMessage()))
                    .collect(Collectors.toList());
        }

        // Use Jackson for JSON serialization
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(
                JsonInclude.Include.NON_NULL); // This will skip null values during serialization
        try {
            this.message = objectMapper.writeValueAsString(new ValidationErrorMessage(validationError, errors));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            this.message = "Error generating validation message";
        }
    }

    public String getMessage() {
        return message;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    private record ValidationErrorMessage(String message, List<ValidationError> errors) {}
}
