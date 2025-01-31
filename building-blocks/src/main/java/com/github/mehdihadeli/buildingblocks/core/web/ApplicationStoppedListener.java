package com.github.mehdihadeli.buildingblocks.core.web;

import java.util.function.Consumer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

public class ApplicationStoppedListener implements ApplicationListener<ContextClosedEvent> {
    private final Consumer<ContextClosedEvent> stoppedContextAction;

    public ApplicationStoppedListener(Consumer<ContextClosedEvent> stoppedContextAction) {
        this.stoppedContextAction = stoppedContextAction;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        stoppedContextAction.accept(event);
    }
}
