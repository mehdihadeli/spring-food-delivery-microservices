package com.github.mehdihadeli.buildingblocks.mediator;

import com.github.mehdihadeli.buildingblocks.core.utils.ReflectionUtils;
import com.github.mehdihadeli.buildingblocks.core.utils.SpringBeanUtils;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.Mediator;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommand;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommandHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.notifications.INotification;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.notifications.INotificationHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.notifications.INotificationPipelineBehavior;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.notifications.NotificationHandlerDelegate;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.IQuery;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.IQueryHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IPipelineBehavior;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IRequest;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IRequestHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.RequestHandlerDelegate;
import com.github.mehdihadeli.buildingblocks.validation.ValidationUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class MediatorImpl implements Mediator {

    private final ApplicationContext applicationContext;

    // Cache maps for request handlers and pipeline behaviors
    private final ConcurrentHashMap<Class<?>, INotificationHandler<?>> notificationHandlerCache =
            new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, IRequestHandler<?, ?>> requestHandlerCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, ICommandHandler<?, ?>> commandHandlerCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, IQueryHandler<?, ?>> queryHandlerCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, List<IPipelineBehavior<?, ?>>> requestPipelineCache =
            new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, List<INotificationPipelineBehavior<?>>> notificationPipelineCache =
            new ConcurrentHashMap<>();

    MediatorImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public <TResponse> TResponse send(IRequest<TResponse> request) throws RuntimeException {
        ValidationUtils.notBeNull(request, "request");

        var requestHandler = resolveRequestHandler(request, applicationContext);
        var pipelineBehaviors = resolveRequestPipelineBehaviors(request, applicationContext);

        // Build the handler chain containing actual handler and all registered pipelines
        RequestHandlerDelegate<TResponse> handlerChain = () -> requestHandler.handle(request);
        for (var behavior : pipelineBehaviors) {
            final RequestHandlerDelegate<TResponse> current = handlerChain;
            handlerChain = () -> behavior.handle(request, current);
        }

        return handlerChain.handle();
    }

    @Override
    public <TResponse> TResponse send(ICommand<TResponse> command) throws RuntimeException {
        ValidationUtils.notBeNull(command, "command");

        var commandHandler = resolveCommandHandler(command, applicationContext);
        var pipelineBehaviors = resolveRequestPipelineBehaviors(command, applicationContext);

        // Build the handler chain containing actual handler and all registered pipelines
        RequestHandlerDelegate<TResponse> handlerChain = () -> commandHandler.handle(command);
        for (var behavior : pipelineBehaviors) {
            final RequestHandlerDelegate<TResponse> current = handlerChain;
            handlerChain = () -> behavior.handle(command, current);
        }

        return handlerChain.handle();
    }

    @Override
    public <TResponse> TResponse send(IQuery<TResponse> query) throws RuntimeException {
        ValidationUtils.notBeNull(query, "query");

        var queryHandler = resolveQueryHandler(query, applicationContext);
        var pipelineBehaviors = resolveRequestPipelineBehaviors(query, applicationContext);

        // Build the handler chain containing actual handler and all registered pipelines
        RequestHandlerDelegate<TResponse> handlerChain = () -> queryHandler.handle(query);
        for (var behavior : pipelineBehaviors) {
            final RequestHandlerDelegate<TResponse> current = handlerChain;
            handlerChain = () -> behavior.handle(query, current);
        }

        return handlerChain.handle();
    }

    @Override
    public <TNotification extends INotification> void publish(TNotification notification) throws RuntimeException {
        ValidationUtils.notBeNull(notification, "notification");

        var notificationHandler = resolveNotificationHandler(notification, applicationContext);
        if (notificationHandler == null) {
            return;
        }

        var notificationPipelineBehaviors = resolveNotificationPipelineBehaviors(notification, applicationContext);

        // Build the handler chain containing actual handler and all registered pipelines
        NotificationHandlerDelegate handlerChain = () -> notificationHandler.handle(notification);
        for (var behavior : notificationPipelineBehaviors) {
            final NotificationHandlerDelegate current = handlerChain;
            handlerChain = () -> behavior.handle(notification, current);
        }

        handlerChain.handle();
    }

    @Override
    public void publish(Object notification) throws RuntimeException {
        ValidationUtils.notBeNull(notification, "notification");

        if (notification instanceof INotification instance) {
            this.publish(instance);
        } else {
            throw new IllegalArgumentException(
                    "notification does not implement " + INotification.class.getSimpleName());
        }
    }

    private <TRequest extends IRequest<TResponse>, TResponse>
            IRequestHandler<TRequest, TResponse> resolveRequestHandler(
                    TRequest request, ApplicationContext applicationContext) {
        return (IRequestHandler<TRequest, TResponse>) requestHandlerCache.computeIfAbsent(
                // request hashmap key (Class<?>)
                request.getClass(),
                // try to get our hash map key in existing dictionary if not exist get it with this lambda
                requestType -> {
                    var responseType = ReflectionUtils.getGenericTypeUsingReflection(request, IRequest.class);

                    String format = String.format(
                            "Not registered a request handler for type: '%s'",
                            request.getClass().getName());

                    if (responseType == null) {
                        throw new IllegalStateException(format);
                    }

                    ResolvableType resolvableType =
                            ResolvableType.forClassWithGenerics(IRequest.class, request.getClass(), responseType);

                    var beanNames = SpringBeanUtils.resolveBeans(applicationContext, resolvableType);

                    // request-response strategy should have `exactly one` handler and if we can't find a corresponding
                    // handler, we should return an error
                    if (beanNames.length == 0) {
                        throw new IllegalStateException(format);
                    }
                    if (beanNames.length > 1) {
                        throw new IllegalStateException(String.format(
                                "Multiple request handlers registered for type: '%s'",
                                request.getClass().getName()));
                    }

                    IRequestHandler<TRequest, TResponse> handler =
                            (IRequestHandler<TRequest, TResponse>) applicationContext.getBean(beanNames[0]);

                    return handler;
                });
    }

    private <TCommand extends ICommand<TResponse>, TResponse>
            ICommandHandler<TCommand, TResponse> resolveCommandHandler(
                    TCommand command, ApplicationContext applicationContext) {
        return (ICommandHandler<TCommand, TResponse>) commandHandlerCache.computeIfAbsent(
                // command hashmap key (Class<?>)
                command.getClass(),
                // try to get our hash map key data in existing dictionary if not exist get it with this lambda
                requestType -> {
                    var responseType = ReflectionUtils.getGenericTypeUsingReflection(command, ICommand.class);

                    String format = String.format(
                            "Not registered a command handler for type: '%s'",
                            command.getClass().getName());

                    if (responseType == null) {
                        throw new IllegalStateException(format);
                    }

                    ResolvableType resolvableType = ResolvableType.forClassWithGenerics(
                            ICommandHandler.class, command.getClass(), responseType);

                    var beanNames = SpringBeanUtils.resolveBeans(applicationContext, resolvableType);

                    // request-response strategy should have `exactly one` handler and if we can't find a corresponding
                    // handler, we should return an error
                    if (beanNames.length == 0) {
                        throw new IllegalStateException(format);
                    }
                    if (beanNames.length > 1) {
                        throw new IllegalStateException(String.format(
                                "Multiple request handlers registered for type: '%s'",
                                command.getClass().getName()));
                    }

                    // Use applicationContext to retrieve the bean
                    ICommandHandler<?, ?> handler = (ICommandHandler<?, ?>) applicationContext.getBean(beanNames[0]);

                    return handler;
                });
    }

    private <TQuery extends IQuery<TResponse>, TResponse> IQueryHandler<TQuery, TResponse> resolveQueryHandler(
            TQuery query, ApplicationContext applicationContext) {
        return (IQueryHandler<TQuery, TResponse>) queryHandlerCache.computeIfAbsent(
                // query hashmap key (Class<?>)
                query.getClass(),
                // try to get our hash map key data in existing dictionary if not exist get it with this lambda
                requestType -> {
                    var responseType = ReflectionUtils.getGenericTypeUsingReflection(query, IQuery.class);

                    String format = String.format(
                            "Not registered a query handler for type: '%s'",
                            query.getClass().getName());

                    if (responseType == null) {
                        throw new IllegalStateException(format);
                    }

                    ResolvableType resolvableType =
                            ResolvableType.forClassWithGenerics(IQueryHandler.class, query.getClass(), responseType);
                    var beanNames = SpringBeanUtils.resolveBeans(applicationContext, resolvableType);

                    // request-response strategy should have exactly one handler and if we can't find a corresponding
                    // handler, we should return an error
                    if (beanNames.length == 0) {
                        throw new IllegalStateException(format);
                    }
                    if (beanNames.length > 1) {
                        throw new IllegalStateException(String.format(
                                "Multiple query handlers registered for type: '%s'",
                                query.getClass().getName()));
                    }

                    // Use applicationContext to retrieve the bean
                    IQueryHandler<?, ?> handler = (IQueryHandler<?, ?>) applicationContext.getBean(beanNames[0]);

                    return handler;
                });
    }

    private <TRequest extends IRequest<TResponse>, TResponse>
            List<IPipelineBehavior<TRequest, TResponse>> resolveRequestPipelineBehaviors(
                    TRequest request, ApplicationContext applicationContext) {

        return requestPipelineCache
                .computeIfAbsent(request.getClass(), requestTypeInput -> {
                    var responseType = ReflectionUtils.getGenericTypeUsingReflection(request, IRequest.class);

                    if (responseType == null) {
                        // Pipelines are optional, return an empty list if no response type is found
                        return Collections.emptyList();
                    }

                    // Resolve IPipelineBehavior beans with generic placeholders for TRequest and TResponse
                    ResolvableType resolvableType =
                            ResolvableType.forClassWithGenerics(IPipelineBehavior.class, Object.class, Object.class);
                    var beanNames = SpringBeanUtils.resolveBeans(applicationContext, resolvableType);

                    if (beanNames.length == 0) {
                        return Collections.emptyList();
                    }

                    List<IPipelineBehavior<?, ?>> rawBehaviors = Stream.of(beanNames)
                            .map(name -> (IPipelineBehavior<?, ?>) applicationContext.getBean(name))
                            .collect(Collectors.toList());

                    Collections.reverse(rawBehaviors);

                    return rawBehaviors;
                })
                .stream()
                .map(behavior -> (IPipelineBehavior<TRequest, TResponse>) behavior)
                .collect(Collectors.toList());
    }

    private <TNotification extends INotification>
            List<INotificationPipelineBehavior<TNotification>> resolveNotificationPipelineBehaviors(
                    TNotification notification, ApplicationContext applicationContext) {

        // Using computeIfAbsent to retrieve or compute the value if absent
        return notificationPipelineCache
                .computeIfAbsent(notification.getClass(), notificationTypeInput -> {
                    // Resolve beans of generic type INotificationPipelineBehavior<Object>
                    ResolvableType resolvableType =
                            ResolvableType.forClassWithGenerics(INotificationPipelineBehavior.class, Object.class);
                    var beanNames = SpringBeanUtils.resolveBeans(applicationContext, resolvableType);

                    if (beanNames.length == 0) {
                        return Collections.emptyList();
                    }

                    List<INotificationPipelineBehavior<?>> rawBehaviors = Stream.of(beanNames)
                            .map(name -> (INotificationPipelineBehavior<?>) applicationContext.getBean(name))
                            .collect(Collectors.toList());

                    Collections.reverse(rawBehaviors);

                    return rawBehaviors;
                })
                .stream()
                .map(behavior -> (INotificationPipelineBehavior<TNotification>) behavior)
                .collect(Collectors.toList());
    }

    private <TNotification extends INotification> @Nullable
            INotificationHandler<TNotification> resolveNotificationHandler(
                    TNotification notification, ApplicationContext applicationContext) {
        var result = notificationHandlerCache.computeIfAbsent(
                // notification hashmap key (Class<?>)
                notification.getClass(),
                // try to get our hash map key in existing dictionary if not exist get it with this lambda
                requestType -> {
                    ResolvableType resolvableType =
                            ResolvableType.forClassWithGenerics(INotificationHandler.class, notification.getClass());
                    var beanNames = SpringBeanUtils.resolveBeans(applicationContext, resolvableType);

                    // notification strategy should have `zero` or `more` handlers, so it should run without any error
                    // if we can't find a corresponding handler
                    if (beanNames.length == 0) {
                        return null;
                    }

                    Object bean = applicationContext.getBean(beanNames[0]);
                    return (bean instanceof INotificationHandler<?> handler) ? handler : null;
                });

        return result != null ? (INotificationHandler<TNotification>) result : null;
    }
}
