package com.github.mehdihadeli.buildingblocks.core.events;

import com.google.common.base.CaseFormat;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.EventEnvelopeMetadata;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IEventEnvelope;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IEventEnvelopeBase;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IMessage;
import com.github.mehdihadeli.buildingblocks.core.utils.TypeMapperUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventEnvelopeFactory {
    public static IEventEnvelopeBase from(Object data, EventEnvelopeMetadata metadata) {
        return new EventEnvelope<>(data, metadata);
    }

    public static <T extends IMessage> IEventEnvelope<T> from(T data, EventEnvelopeMetadata metadata) {
        return new EventEnvelope<>(data, metadata);
    }

    public static <T extends IMessage> IEventEnvelope<T> from(
            T data, UUID correlationId, UUID causationId, Map<String, Object> headers) {
        EventEnvelopeMetadata metadata = new EventEnvelopeMetadata(
                data.messageId(),
                correlationId,
                TypeMapperUtils.addShortTypeName(data.getClass()),
                // Snake case (lowercase with underscores)
                CaseFormat.LOWER_CAMEL.to(
                        CaseFormat.LOWER_UNDERSCORE, data.getClass().getSimpleName()),
                causationId,
                headers != null ? headers : new HashMap<>());

        return from(data, metadata);
    }

    public static IEventEnvelopeBase from(
            Object data, UUID correlationId, UUID causationId, Map<String, Object> headers) {

        // Use Java Reflection to invoke the generic method dynamically
        try {
            Method genericMethod = Arrays.stream(EventEnvelopeFactory.class.getDeclaredMethods())
                    .filter(method -> method.getName().equals("from")
                            && method.getParameterCount() == 4
                            && method.getGenericParameterTypes()[0] != Object.class)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Generic from method not found"));

            Method genericFromMethod = genericMethod
                    .getDeclaringClass()
                    .getDeclaredMethod("from", data.getClass(), EventEnvelopeMetadata.class);

            return (IEventEnvelopeBase) genericFromMethod.invoke(null, data, correlationId, causationId, headers);
        } catch (Exception e) {
            throw new RuntimeException("Error creating event envelope", e);
        }
    }
}
