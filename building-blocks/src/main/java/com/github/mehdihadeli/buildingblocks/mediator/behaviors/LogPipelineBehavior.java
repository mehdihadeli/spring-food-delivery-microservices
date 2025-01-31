package com.github.mehdihadeli.buildingblocks.mediator.behaviors;

import com.github.mehdihadeli.buildingblocks.core.utils.SerializerUtils;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IPipelineBehavior;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IRequest;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.RequestHandlerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogPipelineBehavior<TRequest extends IRequest<TResponse>, TResponse>
        implements IPipelineBehavior<TRequest, TResponse> {

    private static final Logger logger = LoggerFactory.getLogger(LogPipelineBehavior.class);

    @Override
    public TResponse handle(TRequest request, RequestHandlerDelegate<TResponse> next) {
        long startTime = System.currentTimeMillis();

        logger.atInfo()
                .addKeyValue("request", SerializerUtils.serialize(request))
                .log(
                        "[{}] Handle Request of type {}",
                        getClass().getSimpleName(),
                        request.getClass().getSimpleName());

        TResponse response;

        try {
            // Delegate to the next handler in the pipeline
            response = next.handle();
        } catch (Exception ex) {
            logger.atError()
                    .addKeyValue("request", SerializerUtils.serialize(request))
                    .log(
                            "[{}] Error occurred while handling request of type {}, exception={}",
                            getClass().getSimpleName(),
                            request.getClass().getSimpleName(),
                            ex);
            throw ex;
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            logger.atInfo()
                    .addKeyValue("request", SerializerUtils.serialize(request))
                    .log(
                            "[{}] Request of type {} handled in {} ms",
                            getClass().getSimpleName(),
                            request.getClass().getSimpleName(),
                            executionTime);
        }

        return response;
    }
}
