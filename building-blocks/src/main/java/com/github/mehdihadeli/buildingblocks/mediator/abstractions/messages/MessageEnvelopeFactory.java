package com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages;

import com.github.mehdihadeli.buildingblocks.core.utils.TypeMapperUtils;
import com.google.common.base.CaseFormat;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageEnvelopeFactory {
    public static IMessageEnvelopeBase from(Object data, MessageEnvelopeMetadata metadata) {
        return new MessageEnvelope<>(data, metadata);
    }

    public static <T extends IMessage> IMessageEnvelope<T> from(T data, MessageEnvelopeMetadata metadata) {
        return new MessageEnvelope<>(data, metadata);
    }

    public static <T extends IMessage> IMessageEnvelope<T> from(
            T data, UUID correlationId, UUID causationId, Map<String, Object> headers) {
        MessageEnvelopeMetadata metadata = new MessageEnvelopeMetadata(
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

    public static IMessageEnvelopeBase from(
            Object data, UUID correlationId, UUID causationId, Map<String, Object> headers) {

        // Use Java Reflection to invoke the generic method dynamically
        try {
            Method genericMethod = Arrays.stream(MessageEnvelopeFactory.class.getDeclaredMethods())
                    .filter(method -> method.getName().equals("from")
                            && method.getParameterCount() == 4
                            && method.getGenericParameterTypes()[0] != Object.class)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Generic from method not found"));

            Method genericFromMethod = genericMethod
                    .getDeclaringClass()
                    .getDeclaredMethod("from", data.getClass(), MessageEnvelopeMetadata.class);

            return (IMessageEnvelopeBase) genericFromMethod.invoke(null, data, correlationId, causationId, headers);
        } catch (Exception e) {
            throw new RuntimeException("Error creating event envelope", e);
        }
    }
}
