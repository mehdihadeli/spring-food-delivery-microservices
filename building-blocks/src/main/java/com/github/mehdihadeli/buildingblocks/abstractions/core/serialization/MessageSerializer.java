package com.github.mehdihadeli.buildingblocks.abstractions.core.serialization;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IEventEnvelope;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IEventEnvelopeBase;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IMessage;

public interface MessageSerializer {

    String getContentType();

    /**
     * Serializes the given IEventEnvelopeBase into a string.
     *
     * @param eventEnvelope a message envelope that implements the IMessage interface.
     * @return a JSON string for the serialized message envelope.
     */
    String serialize(IEventEnvelopeBase eventEnvelope);

    <T extends IMessage> String serialize(IEventEnvelope<T> eventEnvelope);

    /**
     * Deserialize the given payload into an IEventEnvelope.
     *
     * @param eventEnvelope a JSON string to deserialize.
     * @param <T>          the type of message expected in the envelope.
     * @return returns an IEventEnvelope of type T.
     */
    <T extends IMessage> IEventEnvelope<T> deserialize(String eventEnvelope, Class<?> messageType);
}
