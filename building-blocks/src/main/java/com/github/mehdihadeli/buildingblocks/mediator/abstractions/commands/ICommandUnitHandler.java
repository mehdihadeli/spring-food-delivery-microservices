package com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.Unit;

public interface ICommandUnitHandler<TCommand extends ICommandUnit> extends ICommandHandler<TCommand, Unit> {}
