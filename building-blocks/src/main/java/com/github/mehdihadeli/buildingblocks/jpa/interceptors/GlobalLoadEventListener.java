package com.github.mehdihadeli.buildingblocks.jpa.interceptors;

import org.hibernate.HibernateException;
import org.hibernate.event.spi.LoadEvent;
import org.hibernate.event.spi.LoadEventListener;

public class GlobalLoadEventListener implements LoadEventListener {

    @Override
    public void onLoad(LoadEvent loadEvent, LoadType loadType) throws HibernateException {
        //    Session session = loadEvent.getSession();
        //    Filter filter = session.enableFilter("deletedFilter");
        //    filter.setParameter("deleted", false);
    }
}

