package com.github.mehdihadeli.buildingblocks.abstractions.core.request;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommand;

public interface ITxCommand<TResponse> extends ICommand<TResponse>, ITxRequest {}
