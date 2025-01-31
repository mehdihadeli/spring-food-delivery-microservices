package com.github.mehdihadeli.buildingblocks.core.exceptions;

import com.github.mehdihadeli.buildingblocks.abstractions.core.domain.IBusinessRule;

public class BusinessRuleValidationException extends DomainException {
    private final IBusinessRule brokenRule;
    private final String details;

    public BusinessRuleValidationException(IBusinessRule brokenRule) {
        super(brokenRule.getMessage());
        this.brokenRule = brokenRule;
        this.details = brokenRule.getMessage();
    }

    public IBusinessRule getBrokenRule() {
        return brokenRule;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return brokenRule.getClass().getName() + ": " + brokenRule.getMessage();
    }
}
