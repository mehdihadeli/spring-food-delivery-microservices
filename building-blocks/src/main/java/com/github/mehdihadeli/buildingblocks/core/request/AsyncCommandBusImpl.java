package com.github.mehdihadeli.buildingblocks.core.request;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.MessageMetadataAccessor;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IAsyncCommand;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessagePersistenceService;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.AsyncCommandBus;
import com.github.mehdihadeli.buildingblocks.core.events.EventEnvelopeFactory;
import java.util.HashMap;
import java.util.UUID;

public class AsyncCommandBusImpl implements AsyncCommandBus {
    private final MessagePersistenceService messagePersistenceService;
    private final MessageMetadataAccessor messageMetadataAccessor;

    public AsyncCommandBusImpl(
            MessagePersistenceService messagePersistenceService, MessageMetadataAccessor messageMetadataAccessor) {
        this.messagePersistenceService = messagePersistenceService;
        this.messageMetadataAccessor = messageMetadataAccessor;
    }

    @Override
    public <TCommand extends IAsyncCommand> void sendExternal(TCommand command) {
        UUID correlationId = messageMetadataAccessor.correlationId();
        UUID cautionId = messageMetadataAccessor.cautionId();
        var eventEnvelope = EventEnvelopeFactory.from(command, correlationId, cautionId, new HashMap<>());

        // add message to outbox to run
        messagePersistenceService.addPublishMessage(eventEnvelope);
    }
}
