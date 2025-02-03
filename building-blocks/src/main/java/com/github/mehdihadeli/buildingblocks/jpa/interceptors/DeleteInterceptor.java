package com.github.mehdihadeli.buildingblocks.jpa.interceptors;

import com.github.mehdihadeli.buildingblocks.abstractions.core.data.IEntityDataModelBase;
import jakarta.persistence.PreRemove;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.domain.AuditorAware;

public class DeleteInterceptor {
    private static ApplicationContext applicationContext;

    public AuditorAware<UUID> getAuditorAware() {
        // ResolvableType type = ResolvableType.forClassWithGenerics(AuditorAware.class, UUID.class);
        ParameterizedTypeReference<AuditorAware<UUID>> typeRef = new ParameterizedTypeReference<>() {};

        return (AuditorAware<UUID>) applicationContext
                .getBeanProvider(ResolvableType.forType(typeRef.getType()))
                .getIfAvailable();
    }

    public AuditingHandler getAuditingHandler() {
        return applicationContext.getBean(AuditingHandler.class);
    }

    public DeleteInterceptor() {}

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        DeleteInterceptor.applicationContext = applicationContext;
    }

    @PreRemove
    public void handlePreRemove(Object entity) {
        if (entity instanceof IEntityDataModelBase<?> entityObject) {
            // Prevent physical delete, mark as deleted
            entityObject.setDeleted(true);
            entityObject.setDeletedDate(LocalDateTime.now());
            getAuditorAware().getCurrentAuditor().ifPresent(entityObject::setDeletedByObject);
        }
    }
}
