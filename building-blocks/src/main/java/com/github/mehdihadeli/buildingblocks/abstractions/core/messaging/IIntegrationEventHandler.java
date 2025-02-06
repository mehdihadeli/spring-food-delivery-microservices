package com.github.mehdihadeli.buildingblocks.abstractions.core.messaging;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelopeHandler;

public interface IIntegrationEventHandler<TMessage extends IIntegrationEvent>
        extends IMessageEnvelopeHandler<TMessage> {}
