package com.github.mehdihadeli.catalogs;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.DomainEventPublisher;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.DomainEventsAccessor;
import com.github.mehdihadeli.buildingblocks.jpa.TransactionBehaviorPipeline;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IRequest;
import com.github.mehdihadeli.buildingblocks.mediator.behaviors.LogPipelineBehavior;
import com.github.mehdihadeli.buildingblocks.validation.ValidationPipelineBehavior;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class VerticalSliceTemplateConfiguration {

    @Bean
    @Order(1)
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    @ConditionalOnMissingBean
    public <TRequest extends IRequest<TResponse>, TResponse>
            LogPipelineBehavior<TRequest, TResponse> logPipelineBehavior() {
        return new LogPipelineBehavior<>();
    }

    @Bean
    @Order(2)
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public <TRequest extends IRequest<TResponse>, TResponse>
            ValidationPipelineBehavior<TRequest, TResponse> validationPipelineBehavior(
                    ApplicationContext applicationContext) {
        return new ValidationPipelineBehavior<>(applicationContext);
    }

    @Bean
    @Order(3)
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public <TRequest extends IRequest<TResponse>, TResponse>
            TransactionBehaviorPipeline<TRequest, TResponse> transactionPipelineBehavior(
                    PlatformTransactionManager platformTransactionManager,
                    DomainEventPublisher domainEventPublisher,
                    DomainEventsAccessor domainEventsAccessor) {
        return new TransactionBehaviorPipeline<>(
                domainEventPublisher, domainEventsAccessor, platformTransactionManager);
    }
}
