package com.github.mehdihadeli.buildingblocks.validation;

import org.springframework.validation.Validator;

public abstract class SpringValidator<T> implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
