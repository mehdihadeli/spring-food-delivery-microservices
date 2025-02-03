package com.github.mehdihadeli.buildingblocks.core.data;

import com.github.mehdihadeli.buildingblocks.jpa.interceptors.AggregatesDomainEventsStorageInterceptor;
import com.github.mehdihadeli.buildingblocks.jpa.interceptors.AuditInterceptor;
import com.github.mehdihadeli.buildingblocks.jpa.interceptors.DeleteInterceptor;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.util.UUID;

@MappedSuperclass
@EntityListeners({AuditInterceptor.class, DeleteInterceptor.class, AggregatesDomainEventsStorageInterceptor.class})
public abstract class AggregateDataModel extends AggregateDataModelBase<UUID> {}
