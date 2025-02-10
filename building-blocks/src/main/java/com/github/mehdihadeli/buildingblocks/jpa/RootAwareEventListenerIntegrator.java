package com.github.mehdihadeli.buildingblocks.jpa;

import com.github.mehdihadeli.buildingblocks.jpa.interceptors.CustomHibernateEventListener;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

// https://vladmihalcea.com/spring-hibernate-entity-listeners/
public class RootAwareEventListenerIntegrator implements Integrator {
    public static final RootAwareEventListenerIntegrator INSTANCE = new RootAwareEventListenerIntegrator();

    @Override
    public void integrate(
            Metadata metadata, BootstrapContext bootstrapContext, SessionFactoryImplementor sessionFactory) {
        final EventListenerRegistry eventListenerRegistry =
                sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);

        eventListenerRegistry.appendListeners(EventType.PERSIST, CustomHibernateEventListener.INSTANCE);
        eventListenerRegistry.appendListeners(EventType.PRE_INSERT, CustomHibernateEventListener.INSTANCE);
        eventListenerRegistry.appendListeners(EventType.PRE_UPDATE, CustomHibernateEventListener.INSTANCE);
        //        eventListenerRegistry.appendListeners(EventType.LOAD, new GlobalLoadEventListener());
        //        eventListenerRegistry.appendListeners(EventType.PRE_LOAD, new GlobalPreLoadEventListener());
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {}
}
