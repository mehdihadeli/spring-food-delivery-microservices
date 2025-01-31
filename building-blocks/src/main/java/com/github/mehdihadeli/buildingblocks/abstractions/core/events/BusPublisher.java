package com.github.mehdihadeli.buildingblocks.abstractions.core.events;

import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IMessage;

public interface BusPublisher {

    <TMessage extends IMessage> void publish(TMessage message);

    <TMessage extends IMessage> void publish(IEventEnvelope<TMessage> eventEnvelope);

    <TMessage extends IMessage> void publish(TMessage message, String exchangeOrTopic, String queue);

    <TMessage extends IMessage> void publish(
            IEventEnvelope<TMessage> eventEnvelope, String exchangeOrTopic, String queue);
}
