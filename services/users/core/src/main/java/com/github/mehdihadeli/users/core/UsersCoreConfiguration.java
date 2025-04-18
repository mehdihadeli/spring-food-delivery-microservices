package com.github.mehdihadeli.users.core;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.DomainEventPublisher;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.DomainEventsAccessor;
import com.github.mehdihadeli.buildingblocks.jpa.TransactionBehaviorPipeline;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IRequest;
import com.github.mehdihadeli.buildingblocks.mediator.behaviors.LogPipelineBehavior;
import com.github.mehdihadeli.buildingblocks.observability.behaviors.ObservabilityPipelineBehavior;
import com.github.mehdihadeli.buildingblocks.observability.diagnostics.command.CommandHandlerMetrics;
import com.github.mehdihadeli.buildingblocks.observability.diagnostics.command.CommandHandlerSpan;
import com.github.mehdihadeli.buildingblocks.observability.diagnostics.query.QueryHandlerMetrics;
import com.github.mehdihadeli.buildingblocks.observability.diagnostics.query.QueryHandlerSpan;
import com.github.mehdihadeli.buildingblocks.security.KeycloakCustomClientFactory;
import com.github.mehdihadeli.buildingblocks.security.OAthCustomClientOptions;
import com.github.mehdihadeli.buildingblocks.validation.ValidationPipelineBehavior;
import com.github.mehdihadeli.users.core.users.contracts.KeycloakUserService;
import com.github.mehdihadeli.users.core.users.services.KeycloakUserServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class UsersCoreConfiguration {

    @Bean
    public SecurityFilterChain customSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/v1/users/customer-user") // Match only this specific endpoint
                .csrf(csrf ->
                        csrf.ignoringRequestMatchers("/api/v1/users/customer-user")) // Disable CSRF for this endpoint
                .authorizeHttpRequests(auth -> {
                    auth.anyRequest().permitAll(); // Allow unauthenticated access to this endpoint
                });

        return http.build();
    }

    @Bean
    public KeycloakUserService keycloakUserService(
            KeycloakCustomClientFactory keycloakCustomClientFactory, OAthCustomClientOptions oathCustomClientOptions) {
        return new KeycloakUserServiceImpl(keycloakCustomClientFactory, oathCustomClientOptions);
    }

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
    @ConditionalOnMissingBean
    public <TRequest extends IRequest<TResponse>, TResponse>
            ObservabilityPipelineBehavior<TRequest, TResponse> observabilityPipelineBehavior(
                    CommandHandlerSpan commandHandlerSpan,
                    CommandHandlerMetrics commandHandlerMetrics,
                    QueryHandlerSpan queryHandlerSpan,
                    QueryHandlerMetrics queryHandlerMetrics) {
        return new ObservabilityPipelineBehavior<>(
                commandHandlerSpan, commandHandlerMetrics, queryHandlerSpan, queryHandlerMetrics);
    }

    @Bean
    @Order(3)
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public <TRequest extends IRequest<TResponse>, TResponse>
            ValidationPipelineBehavior<TRequest, TResponse> validationPipelineBehavior(
                    ApplicationContext applicationContext) {
        return new ValidationPipelineBehavior<>(applicationContext);
    }

    @Bean
    @Order(4)
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
