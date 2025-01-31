package com.github.mehdihadeli.buildingblocks.mediator.abstractions;

public interface HaveResponseType<TResponse> {
    Class<?> getResponseType();
}
