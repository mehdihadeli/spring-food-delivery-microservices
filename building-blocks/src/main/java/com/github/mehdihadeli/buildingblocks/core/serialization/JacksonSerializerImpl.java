package com.github.mehdihadeli.buildingblocks.core.serialization;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.mehdihadeli.buildingblocks.abstractions.core.serialization.Serializer;

public class JacksonSerializerImpl implements Serializer {
    private final ObjectMapper objectMapper;

    public JacksonSerializerImpl() {
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
    public String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }

    @Override
    public String serializePretty(Object object) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (Exception e) {
            return "Error serializing object: " + e.getMessage();
        }
    }

    @Override
    public <T> T deserialize(String payload) {
        try {
            return objectMapper.readValue(payload, new com.fasterxml.jackson.core.type.TypeReference<T>() {});
        } catch (Exception e) {
            throw new RuntimeException("Deserialization failed", e);
        }
    }

    @Override
    public Object deserialize(String payload, Class<?> type) {
        try {
            return objectMapper.readValue(payload, type);
        } catch (Exception e) {
            throw new RuntimeException("Deserialization failed", e);
        }
    }
}
