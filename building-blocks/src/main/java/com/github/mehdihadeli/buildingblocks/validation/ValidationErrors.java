package com.github.mehdihadeli.buildingblocks.validation;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrors {
    private final List<ValidationError> errorsList = new ArrayList<>();

    public void addError(String fieldName, String errorMessage) {
        errorsList.add(new ValidationError(fieldName, errorMessage));
    }

    public void addErrors(List<ValidationError> errors) {
        errorsList.addAll(errors.stream().toList());
    }

    public void addObjectErrors(List<ObjectError> errors) {
        for (ObjectError error : errors) {
            addObjectError(error);
        }
    }

    public void addObjectError(ObjectError error) {
        if (error instanceof FieldError fieldError) {
            addError(fieldError.getField(), error.getDefaultMessage());
        } else {
            addError(error.getObjectName(), error.getDefaultMessage());
        }
    }

    public List<ValidationError> getErrors() {
        return errorsList;
    }
}
