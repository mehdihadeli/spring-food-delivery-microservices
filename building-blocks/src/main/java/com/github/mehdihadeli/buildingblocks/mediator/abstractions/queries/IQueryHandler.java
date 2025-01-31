package com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IRequestHandler;

public interface IQueryHandler<TQuery extends IQuery<TResponse>, TResponse> extends IRequestHandler<TQuery, TResponse> {
    TResponse handle(TQuery query) throws RuntimeException;
}
