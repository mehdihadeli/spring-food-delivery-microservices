package com.github.mehdihadeli.buildingblocks.mediator.abstractions;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessage;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelope;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelopeBase;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.notifications.INotification;

public interface IPublisher {
    <TNotification extends INotification> void publish(TNotification notification) throws RuntimeException;

    void publish(Object object) throws RuntimeException;

    <TMessage extends IMessage> void publish(TMessage message) throws RuntimeException;

    <TMessage extends IMessage> void publish(IMessageEnvelope<TMessage> messageEnvelope) throws RuntimeException;

    void publish(IMessageEnvelopeBase messageEnvelopeBase) throws RuntimeException;
}
