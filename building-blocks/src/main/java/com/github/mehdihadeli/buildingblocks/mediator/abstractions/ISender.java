package com.github.mehdihadeli.buildingblocks.mediator.abstractions;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommand;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.IQuery;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IRequest;

public interface ISender {

    <TResponse> TResponse send(IRequest<TResponse> request) throws RuntimeException;

    <TResponse> TResponse send(ICommand<TResponse> command) throws RuntimeException;

    <TResponse> TResponse send(IQuery<TResponse> query) throws RuntimeException;
}
