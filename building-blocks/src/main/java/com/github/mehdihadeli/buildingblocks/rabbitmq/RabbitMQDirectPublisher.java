package com.github.mehdihadeli.buildingblocks.rabbitmq;

import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.BusDirectPublisher;
import com.github.mehdihadeli.buildingblocks.abstractions.core.serialization.MessageSerializer;
import com.github.mehdihadeli.buildingblocks.core.utils.StringUtils;
import com.github.mehdihadeli.buildingblocks.core.utils.TypeMapperUtils;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessage;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelope;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelopeBase;
import com.github.mehdihadeli.buildingblocks.validation.ValidationUtils;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitMQDirectPublisher implements BusDirectPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final MessageSerializer messageSerializer;
    private final RabbitAdmin rabbitAdmin;

    public RabbitMQDirectPublisher(
            RabbitTemplate rabbitTemplate, MessageSerializer messageSerializer, RabbitAdmin rabbitAdmin) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageSerializer = messageSerializer;
        this.rabbitAdmin = rabbitAdmin;
    }

    @Override
    public <TMessage extends IMessage> void publish(IMessageEnvelope<TMessage> eventEnvelope) {
        Class<?> messageType = eventEnvelope.message().getClass();
        String exchangeName = StringUtils.toKebabCase(messageType.getSimpleName()) + "-exchange";
        String routingKey = StringUtils.toKebabCase(messageType.getSimpleName());

        publishInternal(eventEnvelope, exchangeName, routingKey);
    }

    @Override
    public void publish(IMessageEnvelopeBase eventEnvelope) {
        Class<?> messageType = eventEnvelope.message().getClass();
        String exchangeName = StringUtils.toKebabCase(messageType.getSimpleName()) + "-exchange";
        String routingKey = StringUtils.toKebabCase(messageType.getSimpleName());

        publishInternal(eventEnvelope, exchangeName, routingKey);
    }

    @Override
    public <TMessage extends IMessage> void publish(
      IMessageEnvelope<TMessage> eventEnvelope, String exchangeOrTopic, String queue) {
        ensureExchangeAndQueueExist(exchangeOrTopic, queue);
        publishInternal(eventEnvelope, exchangeOrTopic, queue);
    }

    @Override
    public void publish(IMessageEnvelopeBase eventEnvelope, String exchangeOrTopic, String queue) {
        ensureExchangeAndQueueExist(exchangeOrTopic, queue);
        publishInternal(eventEnvelope, exchangeOrTopic, queue);
    }

    private void publishInternal(IMessageEnvelopeBase eventEnvelope, String exchange, String routingKey) {
        try {
            ValidationUtils.notBeNull(eventEnvelope.metadata(), "eventEnvelope.metadata");
            // get messageType from short-type name that we've added in our type-mapper in EventEnvelopeFactory
            var messageType = eventEnvelope.metadata().messageType() != null
                    ? eventEnvelope.metadata().messageType()
                    : TypeMapperUtils.addShortTypeName(eventEnvelope.message().getClass());

            // Create message properties
            MessageProperties properties = new MessageProperties();
            properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            properties.setType(messageType);
            properties.setMessageId(eventEnvelope.metadata().messageId().toString());
            properties.setCorrelationId(eventEnvelope.metadata().correlationId().toString());

            // Add custom headers
            if (eventEnvelope.metadata().headers() != null) {
                eventEnvelope.metadata().headers().forEach(properties::setHeader);
            }
            // Serialize the entire event envelope
            String serializedEnvelope = messageSerializer.serialize(eventEnvelope);
            Message message = new Message(serializedEnvelope.getBytes(), properties);

            // Use RabbitTemplate's send method with mandatory flag
            rabbitTemplate.send(exchange, routingKey, message);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to publish message to exchange: " + exchange + " with routing key: " + routingKey, e);
        }
    }

    private void ensureExchangeAndQueueExist(String exchangeName, String queueName) {
        // Declare exchange
        Exchange exchange = new TopicExchange(exchangeName, true, false);
        rabbitAdmin.declareExchange(exchange);

        // Declare queue
        Queue queue = new Queue(queueName, true);
        rabbitAdmin.declareQueue(queue);

        // Create binding
        Binding binding = new Binding(
                queueName,
                Binding.DestinationType.QUEUE,
                exchangeName,
                queueName, // Use queue name as routing key
                null);
        rabbitAdmin.declareBinding(binding);
    }
}
