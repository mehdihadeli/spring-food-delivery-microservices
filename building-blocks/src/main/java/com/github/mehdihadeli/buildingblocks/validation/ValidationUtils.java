package com.github.mehdihadeli.buildingblocks.validation;

import com.github.mehdihadeli.buildingblocks.core.exceptions.ValidationException;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public final class ValidationUtils {

    private static final Set<String> ALLOWED_CURRENCIES = Set.of("USD", "EUR");

    public static <TRequest> TRequest handleValidation(Validator<TRequest> validator, TRequest request)
            throws ValidationException {

        var validationResult = validator.validate(request);
        if (!validationResult.isValid()) {
            var validationErrors = new ValidationErrors();
            validationErrors.addErrors(validationResult.getErrors());
            throw new ValidationException(new ValidationResultModel<>(validationErrors, request).getMessage());
        }

        return request;
    }

    public static <TRequest> TRequest handleValidation(SpringValidator<TRequest> validator, TRequest request)
            throws ValidationException {

        var validationResult = validator.validateObject(request);
        if (validationResult.hasErrors()) {
            var validationErrors = new ValidationErrors();
            validationErrors.addObjectErrors(validationResult.getAllErrors());
            throw new ValidationException(new ValidationResultModel<>(validationErrors, request).getMessage());
        }

        return request;
    }

    public static String notBeNullOrEmpty(@Nullable String argument, String argumentName) {
        if (argument == null || argument.isEmpty()) {
            throw new ValidationException(argumentName + " cannot be null or empty");
        }
        return argument;
    }

    public static <T> T notBeNull(@Nullable T argument, String argumentName) {
        if (argument == null) {
            throw new ValidationException(argumentName + " cannot be null.");
        }
        return argument;
    }

    public static String notBeEmpty(@Nullable String argument, String argumentName) {
        if (argument == null || argument.isEmpty()) {
            throw new ValidationException(argumentName + " cannot be null or empty.");
        }
        return argument;
    }

    public static String notBeNullOrWhiteSpace(@Nullable String argument, String argumentName) {
        if (argument == null || argument.trim().isEmpty()) {
            throw new ValidationException(argumentName + " cannot be null or whitespace.");
        }
        return argument;
    }

    public static UUID notBeEmpty(@Nullable UUID argument, String argumentName) {
        if (argument == null || argument.equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
            throw new ValidationException(argumentName + " cannot be null or empty.");
        }
        return argument;
    }

    public static int notBeNegativeOrZero(int argument, String argumentName) {
        if (argument <= 0) {
            throw new ValidationException(argumentName + " must be greater than zero.");
        }
        return argument;
    }

    public static int notBeNegative(int argument, String argumentName) {
        if (argument < 0) {
            throw new ValidationException(argumentName + " must be greater than or equal zero.");
        }
        return argument;
    }

    public static BigDecimal notBeNegativeOrZero(BigDecimal argument, String argumentName) {
        if (argument == null || argument.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(argumentName + " must be greater than zero.");
        }
        return argument;
    }

    public static long notBeNegativeOrZero(long argument, String argumentName) {
        if (argument <= 0) {
            throw new ValidationException(argumentName + " must be greater than zero.");
        }
        return argument;
    }

    public static double notBeNegativeOrZero(double argument, String argumentName) {
        if (argument <= 0) {
            throw new ValidationException(argumentName + " must be greater than zero.");
        }
        return argument;
    }

    public static String notBeInvalidEmail(String email, String argumentName) {
        String emailRegex = "^[\\w.-]+@[\\w-]+\\.[a-z]{2,}$";
        if (!Pattern.matches(emailRegex, email)) {
            throw new ValidationException(argumentName + " is not a valid email address.");
        }
        return email;
    }

    public static String notBeInvalidPhoneNumber(String phoneNumber, String argumentName) {
        String phoneRegex = "^[+]?\\d{10,15}$";
        if (!Pattern.matches(phoneRegex, phoneNumber)) {
            throw new ValidationException(argumentName + " is not a valid phone number.");
        }
        return phoneNumber;
    }

    public static String notBeInvalidCurrency(@Nullable String currency, String argumentName) {
        if (currency == null || !ALLOWED_CURRENCIES.contains(currency.toUpperCase())) {
            throw new ValidationException(argumentName + " is not a valid currency.");
        }
        return currency;
    }

    public static <T extends Enum<T>> T notBeEmpty(@Nullable T enumValue, String argumentName) {
        if (enumValue == null) {
            throw new ValidationException(argumentName + " cannot be null.");
        }
        return enumValue;
    }

    public static <T extends Enum<T>> T notBeDefault(@Nullable T enumValue, String argumentName) {
        if (enumValue == null || enumValue.ordinal() == 0) {
            throw new ValidationException(argumentName + " cannot be the default enum value.");
        }
        return enumValue;
    }
}
