package com.github.mehdihadeli.buildingblocks.core.bean;

import com.github.mehdihadeli.buildingblocks.abstractions.core.bean.BeanScopeExecutor;
import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class BeanScopeExecutorImpl implements BeanScopeExecutor {
    private final ApplicationContext applicationContext;

    public BeanScopeExecutorImpl(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Executes the provided action within a scoped context.
     *
     * @param action The action to execute, which accepts a service provider and performs an operation.
     */
    public void executeInScope(Consumer<ApplicationContext> action) {
        // simulates a request scope, create a mock request and set it in the RequestContextHolder
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        try {
            // Execute the action within the scope
            action.accept(applicationContext);
        } finally {
            // Reset the request attributes to clean up the scope
            RequestContextHolder.resetRequestAttributes();
        }
    }

    /**
     * Executes the provided action within a scoped context.
     *
     * @param action The action to execute, which accepts a service provider and returns a result.
     * @param <T>    The type of the result.
     * @return The result of the action.
     */
    public <T> T executeInScope(Function<ApplicationContext, T> action) {
        // simulates a request scope, create a mock request and set it in the RequestContextHolder
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        try {
            // Execute the action within the scope
            return action.apply(applicationContext);
        } finally {
            // Reset the request attributes to clean up the scope
            RequestContextHolder.resetRequestAttributes();
        }
    }
}
