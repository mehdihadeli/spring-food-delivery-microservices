package com.github.mehdihadeli.buildingblocks.core.messaging.messagepersistence;

import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessageDeliveryType;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessagePersistenceRepository;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessageStatus;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.PersistMessage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

public class InMemoryMessagePersistenceRepository implements MessagePersistenceRepository {
    private final Map<UUID, PersistMessage> messageStore = new ConcurrentHashMap<>();

    @Override
    public void add(PersistMessage persistMessage) {
        messageStore.put(persistMessage.getId(), persistMessage);
    }

    @Override
    public void update(PersistMessage persistMessage) {
        messageStore.put(persistMessage.getId(), persistMessage);
    }

    @Override
    public void changeState(UUID messageId, MessageStatus status) {
        PersistMessage message = messageStore.get(messageId);
        if (message != null) {
            message.changeState(status);
            messageStore.put(messageId, message);
        }
    }

    @Override
    public List<PersistMessage> getAll() {
        return new ArrayList<>(messageStore.values());
    }

    @Override
    public List<PersistMessage> getByFilterSpec(Specification<PersistMessage> specification) {
        return getAll();
    }

    @Override
    public List<PersistMessage> filterByState(@Nullable MessageStatus messageStatus) {
        return messageStore.values().stream()
                .filter(m -> m.getMessageStatus() == null || m.getMessageStatus() == messageStatus)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersistMessage> filterBy(
            @Nullable MessageStatus messageStatus,
            @Nullable MessageDeliveryType deliveryType,
            @Nullable String dataType) {
        return messageStore.values().stream()
                .filter(m -> m.getMessageStatus() == null || m.getMessageStatus() == messageStatus)
                .filter(m -> m.getDeliveryType() == null || m.getDeliveryType() == deliveryType)
                .filter(m -> dataType == null || m.getDataType() == dataType)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PersistMessage> getById(UUID id) {
        return Optional.ofNullable(messageStore.get(id));
    }

    @Override
    public boolean remove(PersistMessage persistMessage) {
        return messageStore.remove(persistMessage.getId()) != null;
    }

    @Override
    public void cleanupMessages() {
        messageStore.clear();
    }
}
