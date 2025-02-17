package com.github.mehdihadeli.buildingblocks.observability.diagnostics.command;

public interface CommandHandlerMetrics {
    <TCommand> void startExecuting(Class<TCommand> commandClass);

    <TCommand> void finishExecuting(Class<TCommand> commandClass);

    <TCommand> void failedCommand(Class<TCommand> commandClass);
}
