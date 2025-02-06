package com.github.mehdihadeli.buildingblocks.abstractions.core.events;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessage;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelope;

public interface BusPublisher {

    <TMessage extends IMessage> void publish(TMessage message);

    <TMessage extends IMessage> void publish(IMessageEnvelope<TMessage> eventEnvelope);

    <TMessage extends IMessage> void publish(TMessage message, String exchangeOrTopic, String queue);

    <TMessage extends IMessage> void publish(
      IMessageEnvelope<TMessage> eventEnvelope, String exchangeOrTopic, String queue);
}
