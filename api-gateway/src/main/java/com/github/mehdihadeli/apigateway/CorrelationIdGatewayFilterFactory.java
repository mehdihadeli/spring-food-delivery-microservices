package com.github.mehdihadeli.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CorrelationIdGatewayFilterFactory
        extends AbstractGatewayFilterFactory<CorrelationIdGatewayFilterFactory.Config> {
    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdGatewayFilterFactory.class);

    public CorrelationIdGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Generate a correlation ID (UUID)
            String correlationId = UUID.randomUUID().toString();

            // Log the correlation ID
            logger.info("Generated Correlation ID: {}", correlationId);

            // Add the correlation ID to the request headers
            ServerHttpRequest request = exchange.getRequest()
                    .mutate()
                    .header("X-Correlation-ID", correlationId)
                    .build();

            // Continue with the modified request
            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    public static class Config {
        // Configuration properties (if needed)
    }
}