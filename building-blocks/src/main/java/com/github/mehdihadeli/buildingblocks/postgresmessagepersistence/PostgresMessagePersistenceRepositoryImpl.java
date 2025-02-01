package com.github.mehdihadeli.buildingblocks.postgresmessagepersistence;

import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessagePersistenceRepository;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessageStatus;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.PersistMessage;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.PersistMessage_;
import com.github.mehdihadeli.buildingblocks.core.data.CriteriaQueryUtils;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PostgresMessagePersistenceRepositoryImpl implements MessagePersistenceRepository {
    private final EntityManager entityManager;

    public PostgresMessagePersistenceRepositoryImpl(EntityManager entityManager) {
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
        return CriteriaQueryUtils.findAll(this.entityManager, PersistMessage.class, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersistMessage> getByFilterSpec(Specification<PersistMessage> specification) {
        return CriteriaQueryUtils.findAll(this.entityManager, PersistMessage.class, specification);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PersistMessage> getById(UUID id) {
        return CriteriaQueryUtils.findOne(
                entityManager, PersistMessage.class, (root, query, cb) -> cb.equal(root.get(PersistMessage_.id), id));
    }

    @Override
    @Transactional
    public boolean remove(PersistMessage persistMessage) {
        CriteriaQueryUtils.delete(entityManager, persistMessage);
        return true;
    }

    @Override
    @Transactional
    public void cleanupMessages() {
        CriteriaQueryUtils.deleteAll(entityManager, PersistMessage.class);
    }
}