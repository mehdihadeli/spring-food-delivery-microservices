package com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests;

public interface IRequestHandler<TRequest extends IRequest<TResponse>, TResponse> {
    TResponse handle(TRequest request) throws RuntimeException;
}
