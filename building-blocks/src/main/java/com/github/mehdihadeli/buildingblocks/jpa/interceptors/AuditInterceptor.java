package com.github.mehdihadeli.buildingblocks.jpa.interceptors;

import com.github.mehdihadeli.buildingblocks.abstractions.core.data.IAuditableEntityDataModelBase;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.domain.AuditorAware;

import java.time.LocalDateTime;
import java.util.UUID;

// https://javanexus.com/blog/managing-di-in-jpa-entity-listeners
// https://stackoverflow.com/questions/68393867/how-to-create-custom-auditing-entity-listener-that-extends-auditingentitylistene
// https://stackoverflow.com/questions/12155632/injecting-a-spring-dependency-into-a-jpa-entitylistener/60968523#60968523
public class AuditInterceptor {
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

    public AuditInterceptor() {}

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        AuditInterceptor.applicationContext = applicationContext;
    }

    @PrePersist
    public void setAuditFields(Object entity) {
        if (entity instanceof IAuditableEntityDataModelBase<?> auditable) {
            if (auditable.getCreatedDate() == null) {
                //                AuditingHandler auditHandler = handler.getObject();
                //                if (auditHandler != null) {
                //                    auditHandler.markCreated(auditable);
                //                }
                // or set manually
                auditable.setCreatedDate(LocalDateTime.now());
                getAuditorAware().getCurrentAuditor().ifPresent(auditable::setCreatedByObject);
            }
        }
    }

    @PreUpdate
    public void updateLastModifiedDate(Object entity) {
        if (entity instanceof IAuditableEntityDataModelBase<?> auditable) {
            //            AuditingHandler auditHandler = handler.getObject();
            //            if (auditHandler != null) {
            //                auditHandler.markModified(auditable);
            //            }
            auditable.setLastModifiedDate(LocalDateTime.now());
            getAuditorAware().getCurrentAuditor().ifPresent(auditable::setLastModifiedByObject);
        }
    }
}
