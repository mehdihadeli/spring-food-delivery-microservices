package com.github.mehdihadeli.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayApplication.class);

    public static void main(String[] args) {
        logger.atInfo().addKeyValue("app-name", Constants.APP_NAME).log("Starting {}.", Constants.APP_NAME);
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
