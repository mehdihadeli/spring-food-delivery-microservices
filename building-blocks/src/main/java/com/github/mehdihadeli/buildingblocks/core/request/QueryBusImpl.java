package com.github.mehdihadeli.buildingblocks.core.request;

import com.github.mehdihadeli.buildingblocks.abstractions.core.request.QueryBus;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.Mediator;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.IQuery;

public class QueryBusImpl implements QueryBus {
    private final Mediator mediator;

    public QueryBusImpl(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public <TResponse> TResponse send(IQuery<TResponse> query) {
        return mediator.send(query);
    }
}
