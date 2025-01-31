package com.github.mehdihadeli.buildingblocks.abstractions.core.request;

import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IAsyncCommand;

public interface AsyncCommandBus {
    <TCommand extends IAsyncCommand> void sendExternal(TCommand command);
}
