package com.github.mehdihadeli.buildingblocks.abstractions.core.request;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommand;

import java.util.List;

public interface CommandBus extends AsyncCommandBus {

    <TResult> TResult send(ICommand<TResult> command);

    void schedule(IInternalCommand internalCommandCommand);

    void schedule(List<IInternalCommand> internalCommandCommands);
}
