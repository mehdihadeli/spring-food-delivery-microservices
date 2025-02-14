package com.github.mehdihadeli.buildingblocks.swagger;

import com.github.mehdihadeli.buildingblocks.core.PropertiesService;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.ConfigurableEnvironment;

// when `proxyBeanMethods = false`, avoids the direct method call problem that would occur when one @Bean method calls
// another internally.
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnClass({SwaggerConfig.class, OpenAPI.class, PropertiesService.class})
@ConditionalOnBean({PropertiesService.class})
// @ConditionalOnProperty(prefix = "swagger", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerConfiguration {

    private final PropertiesService propertiesService;
    private final SwaggerProperties swaggerProperties;
    private final ConfigurableEnvironment environment;

    SwaggerConfiguration(
            PropertiesService propertiesService,
            SwaggerProperties swaggerProperties,
            ConfigurableEnvironment environment) {
        this.propertiesService = propertiesService;
        this.swaggerProperties = swaggerProperties;
        this.environment = environment;
    }

    @PostConstruct
    public void postConstruct() {
        propertiesService.updateProperty("springdoc.swagger-ui.path", swaggerProperties.getUiPath());
        propertiesService.updateProperty("springdoc.api-docs.path", swaggerProperties.getOpenapiPath());
    }

    @Bean
    @Scope("singleton")
    // This annotation is used when we want to define a `default bean` that will be created only if there is no other
    // bean already defined that matches the type or name of the bean we are trying to define.
    @ConditionalOnMissingBean
    // it will inject into `OpenAPIService`
    public OpenAPI openAPI(SwaggerProperties swaggerProperties) {
        // Extract the properties from the SwaggerProperties class
        String title = swaggerProperties.getTitle() != null ? swaggerProperties.getTitle() : "API";
        String version = swaggerProperties.getVersion() != null ? swaggerProperties.getVersion() : "1.0";
        String description = swaggerProperties.getDescription() != null ? swaggerProperties.getDescription() : "API";
        String license = swaggerProperties.getLicense() != null ? swaggerProperties.getLicense() : "MIT";
        String licenseUrl = swaggerProperties.getLicenseUrl() != null ? swaggerProperties.getLicenseUrl() : "";

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(
                                "bearerScheme",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer") // Bearer scheme
                                        .bearerFormat("JWT") // Optional: Specify the bearer format (e.g., JWT)
                                        .description("Use `Bearer <token>` for authentication") // Simple help message
                                ))
                .info(new Info()
                        .title(title)
                        .version(version)
                        .description(description)
                        .license(new License().name(license).url(licenseUrl))
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name(swaggerProperties.getContactName())
                                .email(swaggerProperties.getContactEmail())
                                .url(swaggerProperties.getContactUrl())));
    }

    // Define multiple API groups for versioning
    @Bean
    @Scope("singleton")
    public GroupedOpenApi GroupedOpenApiV1() {
        return GroupedOpenApi.builder().group("v1").pathsToMatch("/api/v1/**").build();
    }

    @Bean
    @Scope("singleton")
    public GroupedOpenApi GroupedOpenApiV2() {
        return GroupedOpenApi.builder().group("v2").pathsToMatch("/api/v2/**").build();
    }

    // To replace the existing bean instead of creating a second one, you need to mark your bean as @Primary or
    // use the
    //    // same bean name as the original to override it
    //    @Bean
    //    @Primary
    //    public SwaggerUiConfigProperties customSwaggerUIConfig() {
    //        var uiConfigProperties = new SwaggerUiConfigProperties();
    //        uiConfigProperties.setPath(swaggerProperties.getUiPath());
    //        uiConfigProperties.setEnabled(false);
    //
    //        return uiConfigProperties;
    //    }
    //
    //    @Bean
    //    @Primary
    //    public SpringDocConfigProperties addSpringDocConfigProperties() {
    //        var springDocConfigProperties = new SpringDocConfigProperties();
    //        springDocConfigProperties.getApiDocs().setPath(environment.getProperty("swagger.openapi-path"));
    //        springDocConfigProperties.getApiDocs().setEnabled(false);
    //
    //        return springDocConfigProperties;
    //    }
}
