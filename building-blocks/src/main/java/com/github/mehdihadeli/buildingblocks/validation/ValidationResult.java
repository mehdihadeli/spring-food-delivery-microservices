package com.github.mehdihadeli.buildingblocks.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private final List<ValidationError> errors = new ArrayList<>();

    public void addError(String fieldName, String errorMessage) {
        errors.add(new ValidationError(fieldName, errorMessage));
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
