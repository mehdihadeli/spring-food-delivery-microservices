package com.github.mehdihadeli.buildingblocks.abstractions.core.bean;

import org.springframework.context.ApplicationContext;

import java.util.function.Consumer;
import java.util.function.Function;

public interface BeanScopeExecutor {

    /**
     * Executes the provided action within a scoped context.
     *
     * @param action The action to execute, which accepts a service provider and performs an operation.
     */
    void executeInScope(Consumer<ApplicationContext> action);
    /**
     * Executes the provided action within a scoped context.
     *
     * @param action The action to execute, which accepts a service provider and returns a result.
     * @param <T>    The type of the result.
     * @return The result of the action.
     */
    <T> T executeInScope(Function<ApplicationContext, T> action);
}
