package com.github.mehdihadeli.buildingblocks.jpa.interceptors;

import com.github.mehdihadeli.buildingblocks.abstractions.core.data.SoftDeleteBase;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.domain.AuditorAware;

import java.time.LocalDateTime;
import java.util.UUID;

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

    @PreUpdate
    public void handlePreRemove(Object entity) {
        if (entity instanceof SoftDeleteBase softDeleteBase && softDeleteBase.isDeleted()) {
            // because in jpa like .net we can't change entity state entry to Modified for preventing delete operation
            // and change operation type to update so we have to override Delete JpaRepository and change it to update
            // if is instanceof SoftDeleteBase.
            softDeleteBase.setDeletedDate(LocalDateTime.now());
            getAuditorAware().getCurrentAuditor().ifPresent(softDeleteBase::setDeletedByObject);
        }
    }
}
