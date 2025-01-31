package com.github.mehdihadeli.buildingblocks.jpa.interceptors;

import org.hibernate.HibernateException;
import org.hibernate.event.spi.*;

// https://vladmihalcea.com/spring-hibernate-entity-listeners/
public class CustomHibernateEventListener
        implements PreInsertEventListener, PreUpdateEventListener, PersistEventListener {

    public static final CustomHibernateEventListener INSTANCE = new CustomHibernateEventListener();

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        return false; // Return false to allow the operation to continue
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        return false; // Return false to allow the operation to continue
    }

    @Override
    public void onPersist(PersistEvent persistEvent) throws HibernateException {}

    @Override
    public void onPersist(PersistEvent persistEvent, PersistContext persistContext) throws HibernateException {
        onPersist(persistEvent);
    }
}
