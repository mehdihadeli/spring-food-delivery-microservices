package com.github.mehdihadeli.buildingblocks.core.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IEventEnvelope;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IEventEnvelopeBase;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IMessage;
import com.github.mehdihadeli.buildingblocks.abstractions.core.serialization.MessageSerializer;
import com.github.mehdihadeli.buildingblocks.core.events.EventEnvelope;

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
    public String serialize(IEventEnvelopeBase eventEnvelope) {
        try {
            return objectMapper.writeValueAsString(eventEnvelope);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing event envelope", e);
        }
    }

    @Override
    public <T extends IMessage> String serialize(IEventEnvelope<T> eventEnvelope) {
        try {
            return objectMapper.writeValueAsString(eventEnvelope);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing event envelope", e);
        }
    }

    @Override
    public <T extends IMessage> IEventEnvelope<T> deserialize(String eventEnvelope, Class<?> messageType) {
        try {
            return objectMapper.readValue(
                    eventEnvelope,
                    objectMapper.getTypeFactory().constructParametricType(EventEnvelope.class, messageType));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing event envelope", e);
        }
    }
}