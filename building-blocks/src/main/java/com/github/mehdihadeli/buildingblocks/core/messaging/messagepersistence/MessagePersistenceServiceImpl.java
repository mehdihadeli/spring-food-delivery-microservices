package com.github.mehdihadeli.buildingblocks.core.messaging.messagepersistence;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainNotificationEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.BusDirectPublisher;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.*;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.IInternalCommand;
import com.github.mehdihadeli.buildingblocks.abstractions.core.serialization.MessageSerializer;
import com.github.mehdihadeli.buildingblocks.abstractions.core.serialization.Serializer;
import com.github.mehdihadeli.buildingblocks.core.utils.TypeMapperUtils;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.Mediator;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessage;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelope;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelopeBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class MessagePersistenceServiceImpl implements MessagePersistenceService {
    private static final Logger logger = LoggerFactory.getLogger(MessagePersistenceServiceImpl.class);
    private final MessagePersistenceRepository messagePersistenceRepository;
    private final MessageSerializer messageSerializer;
    private final Mediator mediator;
    private final BusDirectPublisher busDirectPublisher;
    private final Serializer serializer;

    public MessagePersistenceServiceImpl(
            MessagePersistenceRepository messagePersistenceRepository,
            MessageSerializer messageSerializer,
            Mediator mediator,
            BusDirectPublisher busDirectPublisher,
            Serializer serializer) {
        this.messagePersistenceRepository = messagePersistenceRepository;
        this.messageSerializer = messageSerializer;
        this.mediator = mediator;
        this.busDirectPublisher = busDirectPublisher;
        this.serializer = serializer;
    }

    @Override
    public <TMessage extends IMessage> void addPublishMessage(IMessageEnvelope<? extends TMessage> eventEnvelope) {
        addMessageCore(eventEnvelope, MessageDeliveryType.Outbox);
    }

    @Override
    public <TMessage extends IMessage> void addReceivedMessage(IMessageEnvelope<? extends TMessage> eventEnvelope) {
        addMessageCore(eventEnvelope, MessageDeliveryType.Inbox);
    }

    public <TInternalCommand extends IInternalCommand> void addInternalMessage(TInternalCommand internalCommand) {
        PersistMessage persistMessage = new PersistMessage(
                internalCommand.internalCommandId(),
                // because each service has its own persistence and inbox and outbox processor will run in the same
                // process we can use full type name
                TypeMapperUtils.addFullTypeName(internalCommand.getClass()),
                serializer.serialize(internalCommand),
                MessageDeliveryType.Internal);

        messagePersistenceRepository.add(persistMessage);
    }

    public <TDomainEvent extends IDomainEvent, TDomainNotification extends IDomainNotificationEvent<TDomainEvent>>
            void addNotification(TDomainNotification notification) {
        PersistMessage persistMessage = new PersistMessage(
                notification.notificationId(),
                // because each service has its own persistence and inbox and outbox processor will run in the same
                // process we can use full type name
                TypeMapperUtils.addFullTypeName(notification.getClass()),
                serializer.serialize(notification),
                MessageDeliveryType.Internal);

        messagePersistenceRepository.add(persistMessage);
    }

    private <TMessage extends IMessage> void addMessageCore(
            IMessageEnvelope<TMessage> eventEnvelope, MessageDeliveryType deliveryType) {
        if (eventEnvelope.message() == null) {
            throw new IllegalArgumentException("Event envelope message must not be null");
        }

        UUID id = (eventEnvelope.message() instanceof IMessage im) ? im.messageId() : UUID.randomUUID();

        PersistMessage persistMessage = new PersistMessage(
                id,
                // because each service has its own persistence and inbox and outbox processor will run in the same
                // process we can use full type name
                TypeMapperUtils.addFullTypeName(eventEnvelope.message().getClass()),
                messageSerializer.serialize(eventEnvelope),
                deliveryType);

        messagePersistenceRepository.add(persistMessage);

        logger.atInfo()
                .addKeyValue("persistMessage", serializer.serializePretty(persistMessage))
                .log("Message with id: {} and delivery type: {} saved in persistence message store", id, deliveryType);
    }

    @Override
    public void processMessage(UUID messageId) {
        PersistMessage message = messagePersistenceRepository.getById(messageId).orElse(null);

        if (message == null) return;

        switch (message.getDeliveryType()) {
            case Inbox:
                processInbox(message);
                break;
            case Internal:
                processInternal(message);
                break;
            case Outbox:
                processOutbox(message);
                break;
        }

        messagePersistenceRepository.changeState(message.getId(), MessageStatus.Delivered);
    }

    @Override
    public void processAllMessages() {
        List<PersistMessage> messages = messagePersistenceRepository.filterByState(MessageStatus.Stored);

        for (PersistMessage message : messages) {
            processMessage(message.getId());
        }
    }

    private void processOutbox(PersistMessage persistMessage) {
        Class<?> messageType = TypeMapperUtils.getType(persistMessage.getDataType());

        IMessageEnvelope<?> eventEnvelope = messageSerializer.deserialize(persistMessage.getData(), messageType);

        if (eventEnvelope == null) return;

        busDirectPublisher.publish(eventEnvelope);

        logger.atInfo()
                .addKeyValue("persistMessage", serializer.serializePretty(persistMessage))
                .log(
                        "Message with id: {} and delivery type: {} processed from the persistence message store",
                        persistMessage.getId(),
                        persistMessage.getDeliveryType());
    }

    private void processInternal(PersistMessage persistMessage) {
        Class<?> messageType = TypeMapperUtils.getType(persistMessage.getDataType());

        Object internalMessage = serializer.deserialize(persistMessage.getData(), messageType);

        if (internalMessage == null) return;

        if (internalMessage instanceof IDomainNotificationEvent<? extends IDomainEvent> domainNotificationEvent) {
            mediator.publish(domainNotificationEvent);

            logger.atInfo()
                    .addKeyValue("persistMessage", serializer.serializePretty(persistMessage))
                    .log(
                            "Domain-Notification with id: {} and delivery type: {} processed from the persistence message store",
                            persistMessage.getId(),
                            persistMessage.getDeliveryType());
        }

        if (internalMessage instanceof IInternalCommand internalCommand) {
            mediator.send(internalCommand);

            logger.atInfo()
                    .addKeyValue("persistMessage", serializer.serializePretty(persistMessage))
                    .log(
                            "InternalCommand with id: {} and delivery type: {} processed from the persistence message store",
                            persistMessage.getId(),
                            persistMessage.getDeliveryType());
        }
    }

    private void processInbox(PersistMessage persistMessage) {
        Class<?> messageType = TypeMapperUtils.getType(persistMessage.getDataType());
        IMessageEnvelopeBase messageEnvelope = messageSerializer.deserialize(persistMessage.getData(), messageType);

        // if there is any delivery message as inbox we should skip the message
        List<PersistMessage> messages =
                messagePersistenceRepository
                        .filterByStateAndDeliveryType(MessageStatus.Delivered, MessageDeliveryType.Inbox)
                        .stream()
                        .filter(m -> m.getId() == persistMessage.getId())
                        .toList();

        if ((long) messages.size() > 0) {
            return;
        }

        mediator.publish(messageEnvelope);
    }
}
