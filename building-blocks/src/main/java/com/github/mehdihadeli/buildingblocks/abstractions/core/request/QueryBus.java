package com.github.mehdihadeli.buildingblocks.abstractions.core.request;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.IQuery;

public interface QueryBus {
    <TResponse> TResponse send(IQuery<TResponse> query);
}
