package com.github.mehdihadeli.buildingblocks.rabbitmq;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.rabbitmq")
public class CustomRabbitMQProperties {
    private boolean useOutbox = true;

    public boolean isUseOutbox() {
        return useOutbox;
    }

    public void setUseOutbox(boolean useOutbox) {
        this.useOutbox = useOutbox;
    }
}
