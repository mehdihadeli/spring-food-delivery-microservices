package com.github.mehdihadeli.buildingblocks.jpamessagepersistence;

import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.*;
import com.github.mehdihadeli.buildingblocks.core.data.EntityManagerUtils;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JpaMessagePersistenceRepositoryImpl implements MessagePersistenceRepository {
    private final EntityManager entityManager;

    public JpaMessagePersistenceRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void add(PersistMessage persistMessage) {
        persistMessage.markAsNew();
        entityManager.persist(persistMessage);
    }

    @Override
    @Transactional
    public void update(PersistMessage persistMessage) {
        entityManager.merge(persistMessage);
    }

    @Override
    @Transactional
    public void changeState(UUID messageId, MessageStatus status) {
        PersistMessage message = entityManager.find(PersistMessage.class, messageId);
        if (message != null) {
            message.changeState(status);
            entityManager.merge(message);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersistMessage> getAll() {
        return EntityManagerUtils.findAll(
                this.entityManager, PersistMessage.class, (Specification<PersistMessage>) null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersistMessage> getByFilterSpec(Specification<PersistMessage> specification) {
        return EntityManagerUtils.findAll(this.entityManager, PersistMessage.class, specification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersistMessage> filterByState(@Nullable MessageStatus messageStatus) {
        return EntityManagerUtils.findAll(
                this.entityManager, PersistMessage.class, MessageSpecifications.hasStatus(messageStatus));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersistMessage> filterBy(
            @Nullable MessageStatus messageStatus,
            @Nullable MessageDeliveryType deliveryType,
            @Nullable String dataType) {
        return EntityManagerUtils.findAll(
                this.entityManager,
                PersistMessage.class,
                MessageSpecifications.hasStatus(messageStatus)
                        .and(MessageSpecifications.hasDeliveryType(deliveryType))
                        .and(MessageSpecifications.hasDataType(dataType)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PersistMessage> getById(UUID id) {
        return EntityManagerUtils.findOne(
                entityManager, PersistMessage.class, (root, query, cb) -> cb.equal(root.get(PersistMessage_.id), id));
    }

    @Override
    @Transactional
    public boolean remove(PersistMessage persistMessage) {
        EntityManagerUtils.delete(entityManager, persistMessage);
        return true;
    }

    @Override
    @Transactional
    public void cleanupMessages() {
        EntityManagerUtils.deleteAll(entityManager, PersistMessage.class);
    }
}
