package com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests;

@FunctionalInterface
public interface RequestHandlerDelegate<TResponse> {
    TResponse handle() throws RuntimeException;
}
