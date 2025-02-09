package com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

public interface MessagePersistenceRepository {
    void add(PersistMessage persistMessage);

    void update(PersistMessage persistMessage);

    void changeState(UUID messageId, MessageStatus status);

    List<PersistMessage> getAll();

    List<PersistMessage> getByFilterSpec(Specification<PersistMessage> specification);

    List<PersistMessage> filterByState(@Nullable MessageStatus messageStatus);

    List<PersistMessage> filterBy(
            @Nullable MessageStatus messageStatus,
            @Nullable MessageDeliveryType deliveryType,
            @Nullable String datatype);

    Optional<PersistMessage> getById(UUID id);

    boolean remove(PersistMessage persistMessage);

    void cleanupMessages();
}
