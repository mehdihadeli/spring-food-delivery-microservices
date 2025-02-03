package com.github.mehdihadeli.buildingblocks.observability.diagnostics.command;

import com.github.mehdihadeli.buildingblocks.observability.TelemetryTags;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongUpDownCounter;
import io.opentelemetry.api.metrics.Meter;
import java.util.concurrent.TimeUnit;

public class CommandHandlerMetrics {

    private final LongUpDownCounter activeCommandsCounter;
    private final LongCounter totalCommandsNumber;
    private final LongCounter successCommandsNumber;
    private final LongCounter failedCommandsNumber;
    private final DoubleHistogram handlerDuration;

    private long startTime;

    public CommandHandlerMetrics(Meter meter) {
        this.activeCommandsCounter = meter.upDownCounterBuilder("application.commands.active_count")
                .setDescription("Number of commands currently being handled")
                .setUnit("{active_commands}")
                .build();

        this.totalCommandsNumber = meter.counterBuilder("application.commands.total_executed_count")
                .setDescription("Total number of executed commands that sent to command handlers")
                .setUnit("{total_commands}")
                .build();

        this.successCommandsNumber = meter.counterBuilder("application.commands.success_count")
                .setDescription("Number of commands that handled successfully")
                .setUnit("{success_commands}")
                .build();

        this.failedCommandsNumber = meter.counterBuilder("application.commands.failed_count")
                .setDescription("Number of commands that handled with errors")
                .setUnit("{failed_commands}")
                .build();

        this.handlerDuration = meter.histogramBuilder("application.commands.handler_duration")
                .setDescription("Measures the duration of command handler")
                .setUnit("s")
                .build();
    }

    public <TCommand> void startExecuting(Class<TCommand> commandClass) {
        String commandName = commandClass.getSimpleName();
        String commandType = commandClass.getName();
        String commandHandlerName = getCommandHandlerName(commandClass);
        String commandHandlerType = getCommandHandlerType(commandClass);

        Attributes tags = Attributes.builder()
                .put(TelemetryTags.Tracing.Application.Commands.COMMAND, commandName)
                .put(TelemetryTags.Tracing.Application.Commands.COMMAND_TYPE, commandType)
                .put(TelemetryTags.Tracing.Application.Commands.COMMAND_HANDLER, commandHandlerName)
                .put(TelemetryTags.Tracing.Application.Commands.COMMAND_HANDLER_TYPE, commandHandlerType)
                .build();

        activeCommandsCounter.add(1, tags);
        totalCommandsNumber.add(1, tags);

        startTime = System.nanoTime();
    }

    public <TCommand> void finishExecuting(Class<TCommand> commandClass) {
        String commandName = commandClass.getSimpleName();
        String commandType = commandClass.getName();
        String commandHandlerName = getCommandHandlerName(commandClass);
        String commandHandlerType = getCommandHandlerType(commandClass);

        Attributes tags = Attributes.builder()
                .put(TelemetryTags.Tracing.Application.Commands.COMMAND, commandName)
                .put(TelemetryTags.Tracing.Application.Commands.COMMAND_TYPE, commandType)
                .put(TelemetryTags.Tracing.Application.Commands.COMMAND_HANDLER, commandHandlerName)
                .put(TelemetryTags.Tracing.Application.Commands.COMMAND_HANDLER_TYPE, commandHandlerType)
                .build();

        activeCommandsCounter.add(-1, tags);

        long elapsedTime = System.nanoTime() - startTime;
        double elapsedTimeSeconds = TimeUnit.NANOSECONDS.toSeconds(elapsedTime);

        handlerDuration.record(elapsedTimeSeconds, tags);
        successCommandsNumber.add(1, tags);
    }

    public <TCommand> void failedCommand(Class<TCommand> commandClass) {
        String commandName = commandClass.getSimpleName();
        String commandType = commandClass.getName();
        String commandHandlerName = getCommandHandlerName(commandClass);
        String commandHandlerType = getCommandHandlerType(commandClass);

        Attributes tags = Attributes.builder()
                .put(TelemetryTags.Tracing.Application.Commands.COMMAND, commandName)
                .put(TelemetryTags.Tracing.Application.Commands.COMMAND_TYPE, commandType)
                .put(TelemetryTags.Tracing.Application.Commands.COMMAND_HANDLER, commandHandlerName)
                .put(TelemetryTags.Tracing.Application.Commands.COMMAND_HANDLER_TYPE, commandHandlerType)
                .build();

        failedCommandsNumber.add(1, tags);
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
