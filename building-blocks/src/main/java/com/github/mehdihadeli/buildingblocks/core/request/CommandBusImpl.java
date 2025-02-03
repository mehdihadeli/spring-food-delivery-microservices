package com.github.mehdihadeli.buildingblocks.core.request;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.MessageMetadataAccessor;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessagePersistenceService;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.CommandBus;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.IInternalCommand;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.Mediator;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommand;
import java.util.List;

public class CommandBusImpl extends AsyncCommandBusImpl implements CommandBus {
    private final MessagePersistenceService messagePersistenceService;
    private final Mediator mediator;

    public CommandBusImpl(
            Mediator mediator,
            MessagePersistenceService messagePersistenceService,
            MessageMetadataAccessor messageMetadataAccessor) {
        super(messagePersistenceService, messageMetadataAccessor);
        this.messagePersistenceService = messagePersistenceService;
        this.mediator = mediator;
    }

    @Override
    public <TResult> TResult send(ICommand<TResult> command) {
        return mediator.send(command);
    }

    @Override
    public void schedule(IInternalCommand internalCommandCommand) {
        // add internal command to outbox to run
        messagePersistenceService.addInternalMessage(internalCommandCommand);
    }

    @Override
    public void schedule(List<IInternalCommand> internalCommandCommands) {
        for (IInternalCommand internalCommandCommand : internalCommandCommands) {
            schedule(internalCommandCommand);
        }
    }
}
