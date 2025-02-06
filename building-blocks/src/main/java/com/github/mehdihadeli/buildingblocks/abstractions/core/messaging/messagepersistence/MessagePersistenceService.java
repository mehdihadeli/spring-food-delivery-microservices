package com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainNotificationEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.IInternalCommand;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessage;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelope;

import java.util.UUID;

public interface MessagePersistenceService {

    <TMessage extends IMessage> void addPublishMessage(IMessageEnvelope<? extends TMessage> eventEnvelope);

    <TMessage extends IMessage> void addReceivedMessage(IMessageEnvelope<? extends TMessage> eventEnvelope);

    <TInternalCommand extends IInternalCommand> void addInternalMessage(TInternalCommand internalCommand);

    <TDomainEvent extends IDomainEvent, TDomainNotification extends IDomainNotificationEvent<TDomainEvent>>
            void addNotification(TDomainNotification notification);

    void processMessage(UUID messageId);

    void processAllMessages();
}
