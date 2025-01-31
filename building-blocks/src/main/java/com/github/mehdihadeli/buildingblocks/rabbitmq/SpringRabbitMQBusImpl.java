package com.github.mehdihadeli.buildingblocks.rabbitmq;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.ExternalEventBus;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IEventEnvelope;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.MessageMetadataAccessor;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.BusDirectPublisher;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IMessage;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessagePersistenceService;
import com.github.mehdihadeli.buildingblocks.core.events.EventEnvelopeFactory;
import com.github.mehdihadeli.buildingblocks.core.messaging.MessageHeaders;
import com.github.mehdihadeli.buildingblocks.core.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpringRabbitMQBusImpl implements ExternalEventBus {
    private final BusDirectPublisher busDirectPublisher;
    private final MessageMetadataAccessor messageMetadataAccessor;
    private final MessagePersistenceService messagePersistenceService;
    private final CustomRabbitMQProperties customRabbitMQProperties;
    private final String PRIMARY_EXCHANGE_POSTFIX = ".primary_exchange";

    public SpringRabbitMQBusImpl(
            BusDirectPublisher busDirectPublisher,
            MessageMetadataAccessor messageMetadataAccessor,
            MessagePersistenceService messagePersistenceService,
            CustomRabbitMQProperties customRabbitMQProperties) {
        this.busDirectPublisher = busDirectPublisher;
        this.messageMetadataAccessor = messageMetadataAccessor;
        this.messagePersistenceService = messagePersistenceService;
        this.customRabbitMQProperties = customRabbitMQProperties;
    }

    public <TMessage extends IMessage> void publish(TMessage message) {
        UUID correlationId = messageMetadataAccessor.correlationId();
        UUID cautionId = messageMetadataAccessor.cautionId();
        String messageTypeName = StringUtils.toKebabCase(message.getClass().getSimpleName());

        IEventEnvelope<TMessage> eventEnvelope = EventEnvelopeFactory.from(
                message, correlationId, cautionId, createHeaders(messageTypeName, null, null));

        publish(eventEnvelope);
    }

    public <TMessage extends IMessage> void publish(IEventEnvelope<TMessage> eventEnvelope) {
        if (this.customRabbitMQProperties.isUseOutbox()) {
            messagePersistenceService.addPublishMessage(eventEnvelope);
            return;
        }

        busDirectPublisher.publish(eventEnvelope);
    }

    public <TMessage extends IMessage> void publish(TMessage message, String exchangeOrTopic, String queue) {
        UUID correlationId = messageMetadataAccessor.correlationId();
        UUID cautionId = messageMetadataAccessor.cautionId();
        String messageTypeName = StringUtils.toKebabCase(message.getClass().getSimpleName());

        IEventEnvelope<TMessage> eventEnvelope = EventEnvelopeFactory.from(
                message,
                correlationId,
                cautionId,
                createHeaders(
                        messageTypeName,
                        exchangeOrTopic != null ? exchangeOrTopic : messageTypeName + PRIMARY_EXCHANGE_POSTFIX,
                        queue != null ? queue : messageTypeName));

        if (customRabbitMQProperties.isUseOutbox()) {
            messagePersistenceService.addPublishMessage(eventEnvelope);
            return;
        }

        busDirectPublisher.publish(eventEnvelope, exchangeOrTopic, queue);
    }

    public <TMessage extends IMessage> void publish(
            IEventEnvelope<TMessage> eventEnvelope, String exchangeOrTopic, String queue) {
        String messageTypeName =
                StringUtils.toKebabCase(eventEnvelope.message().getClass().getSimpleName());

        if (customRabbitMQProperties.isUseOutbox()) {
            messagePersistenceService.addPublishMessage(eventEnvelope);
            return;
        }

        busDirectPublisher.publish(
                eventEnvelope,
                exchangeOrTopic != null ? exchangeOrTopic : messageTypeName + PRIMARY_EXCHANGE_POSTFIX,
                queue != null ? queue : messageTypeName);
    }

    private Map<String, Object> createHeaders(String messageTypeName, String exchangeOrTopic, String queue) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(MessageHeaders.NAME, messageTypeName);
        headers.put(
                MessageHeaders.EXCHANGE_OR_TOPIC,
                exchangeOrTopic != null ? exchangeOrTopic : messageTypeName + PRIMARY_EXCHANGE_POSTFIX);
        headers.put(MessageHeaders.QUEUE, queue != null ? queue : messageTypeName);
        return headers;
    }
}