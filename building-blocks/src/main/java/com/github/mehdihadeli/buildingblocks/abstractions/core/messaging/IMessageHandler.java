package com.github.mehdihadeli.buildingblocks.abstractions.core.messaging;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IEventEnvelope;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IEventEnvelopeBase;

public interface IMessageHandler<TMessage extends IMessage> {
    void Handle(IEventEnvelope<TMessage> eventEnvelope);

    default void HandleInternal(IEventEnvelopeBase eventEnvelope) {

        Handle((IEventEnvelope<TMessage>) eventEnvelope);
    }
}
