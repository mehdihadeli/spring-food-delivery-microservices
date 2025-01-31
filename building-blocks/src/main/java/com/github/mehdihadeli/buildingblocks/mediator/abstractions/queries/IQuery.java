package com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IRequest;

public interface IQuery<TResponse> extends IBaseQuery, IRequest<TResponse> {}
