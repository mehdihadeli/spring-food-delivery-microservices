package com.github.mehdihadeli.buildingblocks.jpa;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IPipelineBehavior;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IRequest;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.RequestHandlerDelegate;
import jakarta.persistence.EntityManager;
import org.hibernate.Filter;
import org.hibernate.Session;

public class SoftDeleteFilterBehaviorPipeline<TRequest extends IRequest<TResponse>, TResponse>
        implements IPipelineBehavior<TRequest, TResponse> {
    private final EntityManager entityManager;

    public SoftDeleteFilterBehaviorPipeline(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public TResponse handle(TRequest request, RequestHandlerDelegate<TResponse> next) {
        applySoftDeleteFilter();

        return next.handle();
    }

    private void applySoftDeleteFilter() {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedFilter");
        filter.setParameter("deleted", false);
    }
}
