package com.github.mehdihadeli.buildingblocks.validation;

import com.github.mehdihadeli.buildingblocks.core.exceptions.ValidationException;
import com.github.mehdihadeli.buildingblocks.core.utils.SerializerUtils;
import com.github.mehdihadeli.buildingblocks.core.utils.SpringBeanUtils;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IPipelineBehavior;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IRequest;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.RequestHandlerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

public class ValidationPipelineBehavior<TRequest extends IRequest<TResponse>, TResponse>
        implements IPipelineBehavior<TRequest, TResponse> {

    private static final Logger logger = LoggerFactory.getLogger(ValidationPipelineBehavior.class);
    private final ApplicationContext applicationContext;

    public ValidationPipelineBehavior(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public TResponse handle(TRequest request, RequestHandlerDelegate<TResponse> next) {
        SpringValidator<TRequest> springValidator = resolveSpringValidator(request);
        Validator<TRequest> validator = resolveValidator(request);

        logger.atInfo()
                .addKeyValue("request", SerializerUtils.serialize(request))
                .log(
                        "[{}] Handle Request of type {}",
                        getClass().getSimpleName(),
                        request.getClass().getSimpleName());

        if (springValidator != null) {
            try {
                ValidationUtils.handleValidation(springValidator, request);
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
        }

        if (validator != null) {
            try {
                ValidationUtils.handleValidation(validator, request);
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
        }

        var result = next.handle();

        logger.atInfo()
                .addKeyValue("request", SerializerUtils.serialize(request))
                .log(
                        "[{}] Handled Request of type {}",
                        getClass().getSimpleName(),
                        request.getClass().getSimpleName());

        return result;
    }

    private @Nullable Validator<TRequest> resolveValidator(TRequest request) {
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(Validator.class, request.getClass());
        var beanNames = SpringBeanUtils.resolveBeans(applicationContext, resolvableType);

        if (beanNames.length == 0) {
            return null;
        }

        return (Validator<TRequest>) applicationContext.getBean(beanNames[0]);
    }

    private @Nullable SpringValidator<TRequest> resolveSpringValidator(TRequest request) {
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(SpringValidator.class, request.getClass());
        var beanNames = SpringBeanUtils.resolveBeans(applicationContext, resolvableType);

        if (beanNames.length == 0) {
            return null;
        }

        return (SpringValidator<TRequest>) applicationContext.getBean(beanNames[0]);
    }
}
