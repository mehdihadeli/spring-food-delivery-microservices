package com.github.mehdihadeli.buildingblocks.core.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.mehdihadeli.buildingblocks.abstractions.core.serialization.MessageSerializer;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessage;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelope;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelopeBase;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.MessageEnvelope;

public class JacksonMessageSerializerImpl implements MessageSerializer {
    private final ObjectMapper objectMapper;

    public JacksonMessageSerializerImpl() {
        this.objectMapper = new ObjectMapper();
        // Configure the ObjectMapper as needed
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        // we want ISO-8601 format instead of timestamps
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public String serialize(IMessageEnvelopeBase eventEnvelope) {
        try {
            return objectMapper.writeValueAsString(eventEnvelope);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing event envelope", e);
        }
    }

    @Override
    public <T extends IMessage> String serialize(IMessageEnvelope<T> eventEnvelope) {
        try {
            return objectMapper.writeValueAsString(eventEnvelope);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing event envelope", e);
        }
    }

    @Override
    public <T extends IMessage> IMessageEnvelope<T> deserialize(String eventEnvelope, Class<?> messageType) {
        try {
            return objectMapper.readValue(
                    eventEnvelope,
                    objectMapper.getTypeFactory().constructParametricType(MessageEnvelope.class, messageType));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing event envelope", e);
        }
    }
}
