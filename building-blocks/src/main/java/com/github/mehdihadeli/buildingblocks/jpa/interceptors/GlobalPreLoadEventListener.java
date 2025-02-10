package com.github.mehdihadeli.buildingblocks.jpa.interceptors;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.event.spi.PreLoadEvent;
import org.hibernate.event.spi.PreLoadEventListener;

public class GlobalPreLoadEventListener implements PreLoadEventListener {
    @Override
    public void onPreLoad(PreLoadEvent event) {
        Session session = event.getSession();
        Filter filter = session.enableFilter("deletedFilter");
        filter.setParameter("deleted", false);
    }
}
