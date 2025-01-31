package com.github.mehdihadeli.buildingblocks.abstractions.core.messaging;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IEventEnvelope;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IEventEnvelopeBase;

public interface BusDirectPublisher {
    <TMessage extends IMessage> void publish(IEventEnvelope<TMessage> eventEnvelope);

    void publish(IEventEnvelopeBase eventEnvelope);

    <TMessage extends IMessage> void publish(
            IEventEnvelope<TMessage> eventEnvelope, String exchangeOrTopic, String queue);

    void publish(IEventEnvelopeBase eventEnvelope, String exchangeOrTopic, String queue);
}
