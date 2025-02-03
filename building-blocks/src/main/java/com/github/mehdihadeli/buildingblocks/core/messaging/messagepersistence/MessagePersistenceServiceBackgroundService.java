package com.github.mehdihadeli.buildingblocks.core.messaging.messagepersistence;

import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessagePersistenceService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class MessagePersistenceServiceBackgroundService implements InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(MessagePersistenceServiceBackgroundService.class);

    private final ObjectProvider<MessagePersistenceService> messagePersistenceServiceProvider;
    private final MessagePersistenceProperties properties;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledTask;

    public MessagePersistenceServiceBackgroundService(
            ObjectProvider<MessagePersistenceService> messagePersistenceServiceProvider,
            MessagePersistenceProperties properties) {
        this.messagePersistenceServiceProvider = messagePersistenceServiceProvider;
        this.properties = properties;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void afterPropertiesSet() {
        start();
    }

    @Override
    public void destroy() {
        stop();
    }

    public void start() {

        int interval = properties.getInterval() != null ? properties.getInterval() : 30;
        logger.info("MessagePersistenceServiceBackgroundService started with interval: {} seconds", interval);

        scheduledTask = scheduler.scheduleWithFixedDelay(
                () -> {
                    try {
                        logger.info(
                                "MessagePersistenceServiceBackgroundService is running with interval: {} seconds",
                                interval);

                        // simulates a request scope for the background task
                        RequestContextHolder.setRequestAttributes(
                                new ServletRequestAttributes(new MockHttpServletRequest()));

                        // Get a new instance of the service for each execution
                        MessagePersistenceService scopedService = messagePersistenceServiceProvider.getObject();
                        scopedService.processAllMessages();
                    } catch (Exception e) {
                        logger.error("Error in scheduled processing", e);
                    } finally {
                        // ensures that the request attributes are cleared after the task completes, allowing the
                        // `MessagePersistenceService` instance to be disposed properly
                        RequestContextHolder.resetRequestAttributes();
                    }
                },
                0, // initial delay
                interval, // period
                TimeUnit.SECONDS);
    }

    public void stop() {
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(true);
        }
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("MessagePersistenceServiceBackgroundService stopped");
    }
}
