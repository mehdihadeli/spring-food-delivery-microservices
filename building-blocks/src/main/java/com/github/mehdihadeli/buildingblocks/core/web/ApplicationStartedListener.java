package com.github.mehdihadeli.buildingblocks.core.web;

import java.util.function.Consumer;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

// https://medium.com/javarevisited/java-8s-consumer-predicate-supplier-and-function-bbc609a29ff9

// we can add it to `addListeners` of `SpringApplication` or register it as a new bean with @Component
public class ApplicationStartedListener implements ApplicationListener<ApplicationStartedEvent> {
    private final Consumer<ApplicationStartedEvent> startedEventAction;

    public ApplicationStartedListener(Consumer<ApplicationStartedEvent> startedEventAction) {
        this.startedEventAction = startedEventAction;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        startedEventAction.accept(event);
    }
}
