package com.github.mehdihadeli.buildingblocks.abstractions.core.serialization;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessage;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelope;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelopeBase;

public interface MessageSerializer {

    String getContentType();

    /**
     * Serializes the given IEventEnvelopeBase into a string.
     *
     * @param eventEnvelope a message envelope that implements the IMessage interface.
     * @return a JSON string for the serialized message envelope.
     */
    String serialize(IMessageEnvelopeBase eventEnvelope);

    <T extends IMessage> String serialize(IMessageEnvelope<T> eventEnvelope);

    /**
     * Deserialize the given payload into an IEventEnvelope.
     *
     * @param eventEnvelope a JSON string to deserialize.
     * @param <T>          the type of message expected in the envelope.
     * @return returns an IEventEnvelope of type T.
     */
    <T extends IMessage> IMessageEnvelope<T> deserialize(String eventEnvelope, Class<?> messageType);
}
