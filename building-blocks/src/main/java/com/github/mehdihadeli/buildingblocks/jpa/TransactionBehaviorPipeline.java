package com.github.mehdihadeli.buildingblocks.jpa;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.DomainEventPublisher;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.DomainEventsAccessor;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.ITxRequest;
import com.github.mehdihadeli.buildingblocks.core.utils.SerializerUtils;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IPipelineBehavior;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IRequest;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.RequestHandlerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

// https://www.baeldung.com/spring-programmatic-transaction-management

public class TransactionBehaviorPipeline<TRequest extends IRequest<TResponse>, TResponse>
        implements IPipelineBehavior<TRequest, TResponse> {

    private static final Logger logger = LoggerFactory.getLogger(TransactionBehaviorPipeline.class);
    private final DomainEventPublisher domainEventPublisher;
    private final DomainEventsAccessor domainEventsAccessor;
    private final TransactionTemplate transactionTemplate;

    public TransactionBehaviorPipeline(
            DomainEventPublisher domainEventPublisher,
            DomainEventsAccessor domainEventsAccessor,
            PlatformTransactionManager transactionManager) {
        this.domainEventPublisher = domainEventPublisher;
        this.domainEventsAccessor = domainEventsAccessor;

        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setTimeout(1000);
    }

    @Override
    public TResponse handle(TRequest request, RequestHandlerDelegate<TResponse> next) {
        logger.atInfo()
                .addKeyValue("request", SerializerUtils.serializePretty(request))
                .log(
                        "[{}] Handle Request of type {}",
                        getClass().getSimpleName(),
                        request.getClass().getSimpleName());

        // tx should not be called for queries
        if (!(request instanceof ITxRequest)) {
            return next.handle();
        }

        logger.atInfo()
                .addKeyValue("request", SerializerUtils.serializePretty(request))
                .log(
                        "[{}] Opening transaction for {}",
                        getClass().getSimpleName(),
                        request.getClass().getSimpleName());

        var result = transactionTemplate.execute(status -> {
            try {
                logger.atInfo()
                        .addKeyValue("request", SerializerUtils.serializePretty(request))
                        .log(
                                "[{}] Opening transaction for {}",
                                getClass().getSimpleName(),
                                request.getClass().getSimpleName());

                TResponse response = next.handle();

                var domainEvents = domainEventsAccessor.dequeueUncommittedDomainEvents();
                domainEventPublisher.Publish(domainEvents);

                return response;
            } catch (Exception e) {
                logger.atInfo()
                        .addKeyValue("request", SerializerUtils.serializePretty(request))
                        .log(
                                "[{}] Transaction rolled back for {}",
                                getClass().getSimpleName(),
                                request.getClass().getSimpleName());
                status.setRollbackOnly();
                throw e;
            }
        });

        logger.atInfo()
                .addKeyValue("request", SerializerUtils.serializePretty(request))
                .log(
                        "[{}] Transaction completed for {}",
                        getClass().getSimpleName(),
                        request.getClass().getSimpleName());

        return result;
    }
}
