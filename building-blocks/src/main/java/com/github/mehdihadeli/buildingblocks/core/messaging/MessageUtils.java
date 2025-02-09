package com.github.mehdihadeli.buildingblocks.core.messaging;

import com.github.f4b6a3.ulid.UlidCreator;
import com.github.mehdihadeli.buildingblocks.abstractions.core.serialization.MessageSerializer;
import com.github.mehdihadeli.buildingblocks.core.utils.StringUtils;
import com.github.mehdihadeli.buildingblocks.core.utils.TypeMapperUtils;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessage;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelope;
import java.util.Map;
import java.util.UUID;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

public class MessageUtils {
    private MessageUtils() {}

    public static UUID generateMessageId() {
        return UlidCreator.getUlid().toUuid();
    }

    public static UUID generateCorrelationId() {
        return UlidCreator.getUlid().toUuid();
    }

    public static UUID generateNotificationId() {
        return UlidCreator.getUlid().toUuid();
    }

    public static UUID generateInternalId() {
        return UlidCreator.getUlid().toUuid();
    }

    public static String getQueueName(Class<?> clazz) {
        return "%s-queue".formatted(StringUtils.toKebabCase(clazz.getSimpleName()));
    }

    public static String getExchangeName(Class<?> clazz) {
        return "%s-exchange".formatted(StringUtils.toKebabCase(clazz.getSimpleName()));
    }

    public static String getTopicName(Class<?> clazz) {
        return "%s-topic".formatted(StringUtils.toKebabCase(clazz.getSimpleName()));
    }

    public static String getFullTypeName(Class<?> clazz) {
        return clazz.getName();
    }

    public static String getShortTypeName(Class<?> clazz) {
        return clazz.getSimpleName();
    }

    public static <TMessage extends IMessage> IMessageEnvelope<TMessage> convertMessageToEventEnvelope(
            Message message, Class<?> messageType, MessageSerializer messageSerializer) {

        MessageProperties props = message.getMessageProperties();
        Map<String, Object> headers = props.getHeaders();

        // Object messageBody = messageConverter.fromMessage(message);
        // our message body is EventEnvelope
        IMessageEnvelope<TMessage> result = messageSerializer.deserialize(new String(message.getBody()), messageType);
        if (result.metadata().headers() != null) {
            result.metadata().headers().putAll(headers);
        }

        return result;
    }

    public static <TMessage extends IMessage> IMessageEnvelope<TMessage> convertMessageToEventEnvelope(
            Message message, MessageSerializer messageSerializer) {

        MessageProperties props = message.getMessageProperties();
        Map<String, Object> headers = props.getHeaders();

        var inputMessageTypeName = props.getType();
        var inputMessageType = TypeMapperUtils.getType(inputMessageTypeName);

        // Object messageBody = messageConverter.fromMessage(message);
        // our message body is EventEnvelope
        IMessageEnvelope<TMessage> result =
                messageSerializer.deserialize(new String(message.getBody()), inputMessageType);
        if (result.metadata().headers() != null) {
            result.metadata().headers().putAll(headers);
        }

        return result;
    }
}
