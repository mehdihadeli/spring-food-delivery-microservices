package com.github.mehdihadeli.buildingblocks.observability.behaviors;

import com.github.mehdihadeli.buildingblocks.core.utils.SerializerUtils;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.IBaseCommand;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.IBaseQuery;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IPipelineBehavior;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IRequest;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.RequestHandlerDelegate;
import com.github.mehdihadeli.buildingblocks.observability.diagnostics.command.CommandHandlerMetrics;
import com.github.mehdihadeli.buildingblocks.observability.diagnostics.command.CommandHandlerSpan;
import com.github.mehdihadeli.buildingblocks.observability.diagnostics.query.QueryHandlerMetrics;
import com.github.mehdihadeli.buildingblocks.observability.diagnostics.query.QueryHandlerSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObservabilityPipelineBehavior<TRequest extends IRequest<TResponse>, TResponse>
        implements IPipelineBehavior<TRequest, TResponse> {

    private static final Logger logger = LoggerFactory.getLogger(ObservabilityPipelineBehavior.class);
    private final CommandHandlerSpan commandHandlerSpan;
    private final CommandHandlerMetrics commandMetrics;
    private final QueryHandlerSpan queryHandlerSpan;
    private final QueryHandlerMetrics queryMetrics;

    public ObservabilityPipelineBehavior(
            CommandHandlerSpan commandActivity,
            CommandHandlerMetrics commandMetrics,
            QueryHandlerSpan queryHandlerSpan,
            QueryHandlerMetrics queryMetrics) {
        this.commandHandlerSpan = commandActivity;
        this.commandMetrics = commandMetrics;
        this.queryHandlerSpan = queryHandlerSpan;
        this.queryMetrics = queryMetrics;
    }

    @Override
    public TResponse handle(TRequest request, RequestHandlerDelegate<TResponse> next) {
        var isCommand = request instanceof IBaseCommand;
        var isQuery = request instanceof IBaseQuery;

        logger.atInfo()
                .addKeyValue("request", SerializerUtils.serialize(request))
                .log(
                        "[{}] Handle Request of type {}",
                        getClass().getSimpleName(),
                        request.getClass().getSimpleName());

        if (isCommand) {
            commandMetrics.startExecuting(request.getClass());
        }

        if (isQuery) {
            queryMetrics.startExecuting(request.getClass());
        }

        try {
            if (isCommand) {
                var commandResult = commandHandlerSpan.execute(request.getClass(), span -> {
                    return next.handle();
                });

                commandMetrics.finishExecuting(request.getClass());

                return commandResult;
            }

            if (isQuery) {
                var queryResult = queryHandlerSpan.execute(request.getClass(), span -> next.handle());

                queryMetrics.finishExecuting(request.getClass());

                return queryResult;
            }
        } catch (Exception exception) {
            if (isQuery) {
                queryMetrics.failedQuery(request.getClass());
            }

            if (isCommand) {
                commandMetrics.failedCommand(request.getClass());
            }

            throw exception;
        }

        return next.handle();
    }
}