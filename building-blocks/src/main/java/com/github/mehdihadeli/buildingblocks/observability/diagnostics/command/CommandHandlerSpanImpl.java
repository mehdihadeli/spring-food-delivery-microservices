package com.github.mehdihadeli.buildingblocks.observability.diagnostics.command;

import com.github.mehdihadeli.buildingblocks.abstractions.observability.CreateSpanInfo;
import com.github.mehdihadeli.buildingblocks.abstractions.observability.DiagnosticsProvider;
import com.github.mehdihadeli.buildingblocks.observability.ObservabilityConstant;
import com.github.mehdihadeli.buildingblocks.observability.TelemetryTags;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class CommandHandlerSpanImpl implements CommandHandlerSpan {
    private final DiagnosticsProvider diagnosticsProvider;

    public CommandHandlerSpanImpl(DiagnosticsProvider diagnosticsProvider) {
        this.diagnosticsProvider = diagnosticsProvider;
    }

    @Override
    public <TCommand> void execute(Class<TCommand> commandType, Consumer<Span> action) {
        String commandName = commandType.getSimpleName();
        String commandFullName = commandType.getName();
        String commandHandlerName = getCommandHandlerName(commandType);
        String commandHandlerFullName = getCommandHandlerType(commandType);

        String spanName = String.format(
                "%s.%s/%s", ObservabilityConstant.Components.COMMAND_HANDLER, commandHandlerName, commandName);

        CreateSpanInfo activityInfo = new CreateSpanInfo();
        activityInfo.setName(spanName);
        activityInfo.setActivityKind(SpanKind.CONSUMER);

        // Add tags
        Map<String, Object> tags = new HashMap<>();
        tags.put(TelemetryTags.Tracing.Application.Commands.COMMAND, commandName);
        tags.put(TelemetryTags.Tracing.Application.Commands.COMMAND_TYPE, commandFullName);
        tags.put(TelemetryTags.Tracing.Application.Commands.COMMAND_HANDLER, commandHandlerName);
        tags.put(TelemetryTags.Tracing.Application.Commands.COMMAND_HANDLER_TYPE, commandHandlerFullName);
        activityInfo.setTags(tags);

        diagnosticsProvider.executeSpan(activityInfo, action);
    }

    @Override
    public <TCommand, TResult> TResult execute(Class<TCommand> commandType, Function<Span, TResult> action) {
        String commandName = commandType.getSimpleName();
        String commandFullName = commandType.getName();
        String commandHandlerName = getCommandHandlerName(commandType);
        String commandHandlerFullName = getCommandHandlerType(commandType);

        // Create the activity name
        String spanName = String.format(
                "%s.%s/%s", ObservabilityConstant.Components.COMMAND_HANDLER, commandHandlerName, commandName);

        CreateSpanInfo activityInfo = new CreateSpanInfo();
        activityInfo.setName(spanName);
        activityInfo.setActivityKind(SpanKind.CONSUMER);

        // Add tags
        Map<String, Object> tags = new HashMap<>();
        tags.put(TelemetryTags.Tracing.Application.Commands.COMMAND, commandName);
        tags.put(TelemetryTags.Tracing.Application.Commands.COMMAND_TYPE, commandFullName);
        tags.put(TelemetryTags.Tracing.Application.Commands.COMMAND_HANDLER, commandHandlerName);
        tags.put(TelemetryTags.Tracing.Application.Commands.COMMAND_HANDLER_TYPE, commandHandlerFullName);
        activityInfo.setTags(tags);

        return diagnosticsProvider.executeSpan(activityInfo, action);
    }

    /**
     * Gets the name of the command handler.
     *
     * @param commandClass The command class.
     * @return The name of the command handler.
     */
    private <TCommand> String getCommandHandlerName(Class<TCommand> commandClass) {
        return commandClass.getEnclosingClass() != null
                ? commandClass.getEnclosingClass().getSimpleName()
                : commandClass.getSimpleName();
    }

    /**
     * Gets the fully qualified name of the command handler type.
     *
     * @param commandClass The command class.
     * @return The fully qualified name of the command handler type.
     */
    private <TCommand> String getCommandHandlerType(Class<TCommand> commandClass) {
        return commandClass.getEnclosingClass() != null
                ? commandClass.getEnclosingClass().getName()
                : commandClass.getName();
    }
}
