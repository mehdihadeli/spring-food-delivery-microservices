package com.github.mehdihadeli.buildingblocks.abstractions.core.domain;

public interface IBusinessRule {
    boolean isBroken();

    String getMessage();
}
