package com.github.mehdihadeli.buildingblocks.postgresmessagepersistence;

import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessageStatus;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.PersistMessage;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.PersistMessage_;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MessageSpecifications {
    public static Specification<PersistMessage> hasStatus(MessageStatus status) {
        return (root, query, cb) -> cb.equal(root.get(PersistMessage_.messageStatus), status);
    }

    public static Specification<PersistMessage> createdAfter(LocalDateTime date) {
        return (root, query, cb) -> cb.greaterThan(root.get(PersistMessage_.CREATED), date);
    }

    // Specification for not processed messages
    public static Specification<PersistMessage> notDelivered() {
        return (root, query, cb) -> cb.equal(root.get(PersistMessage_.messageStatus), MessageStatus.Stored);
    }

    // Specification for delivered messages
    public static Specification<PersistMessage> delivered() {
        return (root, query, cb) -> cb.equal(root.get(PersistMessage_.messageStatus), MessageStatus.Delivered);
    }

    // Combine specifications
    public static Specification<PersistMessage> statusAndDate(MessageStatus status, LocalDateTime date) {
        return Specification.where(hasStatus(status)).and(createdAfter(date));
    }
}