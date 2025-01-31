package com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IRequest;

public interface ICommand<TResponse> extends IRequest<TResponse>, IBaseCommand {}
