package com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessagePersistenceRepository {
    void add(PersistMessage persistMessage);

    void update(PersistMessage persistMessage);

    void changeState(UUID messageId, MessageStatus status);

    List<PersistMessage> getAll();

    List<PersistMessage> getByFilterSpec(Specification<PersistMessage> specification);

    List<PersistMessage> filterByState(MessageStatus messageStatus);

    List<PersistMessage> filterByStateAndDeliveryType(MessageStatus messageStatus, MessageDeliveryType deliveryType);

    Optional<PersistMessage> getById(UUID id);

    boolean remove(PersistMessage persistMessage);

    void cleanupMessages();
}
