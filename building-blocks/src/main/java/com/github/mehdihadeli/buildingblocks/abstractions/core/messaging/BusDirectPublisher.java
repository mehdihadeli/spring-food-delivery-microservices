package com.github.mehdihadeli.buildingblocks.abstractions.core.messaging;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessage;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelope;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelopeBase;

public interface BusDirectPublisher {
    <TMessage extends IMessage> void publish(IMessageEnvelope<TMessage> eventEnvelope);

    void publish(IMessageEnvelopeBase eventEnvelope);

    <TMessage extends IMessage> void publish(
            IMessageEnvelope<TMessage> eventEnvelope, String exchangeOrTopic, String queue);

    void publish(IMessageEnvelopeBase eventEnvelope, String exchangeOrTopic, String queue);
}
