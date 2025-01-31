package com.github.mehdihadeli.buildingblocks.core.data;

// import com.mehdihadeli.buildingblocks.jpa.interceptors.AggregatesDomainEventsStorageInterceptor;

import com.github.mehdihadeli.buildingblocks.jpa.interceptors.AggregatesDomainEventsStorageInterceptor;
import com.github.mehdihadeli.buildingblocks.jpa.interceptors.AuditInterceptor;
import com.github.mehdihadeli.buildingblocks.jpa.interceptors.DeleteInterceptor;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import java.util.UUID;

@EntityListeners({AuditInterceptor.class, DeleteInterceptor.class, AggregatesDomainEventsStorageInterceptor.class})
@MappedSuperclass
public abstract class AuditableAggregateDataModel extends AuditableAggregateDataModelBase<UUID> {}
