package com.github.mehdihadeli.buildingblocks.observability.diagnostics.command;

import io.opentelemetry.api.trace.Span;
import java.util.function.Consumer;
import java.util.function.Function;

public interface CommandHandlerSpan {
    /**
     * Executes a command and creates a span for tracing.
     *
     * @param action The action to execute.
     */
    <TCommand> void execute(Class<TCommand> commandType, Consumer<Span> action);

    /**
     * Executes a command and creates a span for tracing, returning a result.
     *
     * @param action The action to execute.
     * @param <TResult> The type of the result.
     * @return A CompletionStage representing the asynchronous operation with the result.
     */
    <TCommand, TResult> TResult execute(Class<TCommand> commandType, Function<Span, TResult> action);
}
