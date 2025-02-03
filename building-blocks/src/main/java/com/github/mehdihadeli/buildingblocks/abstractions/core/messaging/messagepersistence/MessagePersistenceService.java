package com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainNotificationEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IEventEnvelope;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IMessage;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.IInternalCommand;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public interface MessagePersistenceService {

    List<PersistMessage> getByFilterSpec(Specification<PersistMessage> specification);

    <TMessage extends IMessage> void addPublishMessage(IEventEnvelope<? extends TMessage> eventEnvelope);

    <TMessage extends IMessage> void addReceivedMessage(IEventEnvelope<? extends TMessage> eventEnvelope);

    <TInternalCommand extends IInternalCommand> void addInternalMessage(TInternalCommand internalCommand);

    <TDomainEvent extends IDomainEvent, TDomainNotification extends IDomainNotificationEvent<TDomainEvent>>
            void addNotification(TDomainNotification notification);

    void processMessage(UUID messageId);

    void processAllMessages();
}
