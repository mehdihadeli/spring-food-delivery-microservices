package com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IRequestHandler;

public interface ICommandHandler<TCommand extends ICommand<TResponse>, TResponse>
        extends IRequestHandler<TCommand, TResponse> {
    TResponse handle(TCommand command) throws RuntimeException;
}
