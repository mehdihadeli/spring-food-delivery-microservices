package com.github.mehdihadeli.apigateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class KeycloakGatewayConfiguration {

    @Bean
    public SecurityWebFilterChain gatewaySecurityFilterChain(ServerHttpSecurity http) {
        return http.cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Apply custom authorization logic for the catalogs path
                        .pathMatchers("/api/{version}/catalogs/{remainder}")
                        .access((authentication, context) -> ReactiveAuthorityUtils.hasAnyAuthority(
                                        "PERMISSION_CATALOGS.READ", "CLAIM_CATALOGS.READ")
                                .flatMap(hasAuthority -> {
                                    if (hasAuthority) {
                                        return Mono.just(new AuthorizationDecision(true));
                                    }
                                    return ReactiveAuthorityUtils.hasAnyRole("CATALOGS:READ", "ADMIN", "CUSTOMER")
                                            .map(AuthorizationDecision::new);
                                }))

                        // Apply custom authorization logic for the customers path
                        .pathMatchers("/api/{version}/customers/{remainder}")
                        .access((authentication, context) -> ReactiveAuthorityUtils.hasAnyAuthority(
                                        "PERMISSION_CUSTOMERS.READ", "CLAIM_CUSTOMERS.READ")
                                .flatMap(hasAuthority -> {
                                    if (hasAuthority) {
                                        return Mono.just(new AuthorizationDecision(true));
                                    }
                                    return ReactiveAuthorityUtils.hasAnyRole("CUSTOMERS:READ", "ADMIN", "CUSTOMER")
                                            .map(AuthorizationDecision::new);
                                }))

                        // Apply custom authorization logic for the users path
                        .pathMatchers("/api/{version}/users/{remainder}")
                        .access((authentication, context) -> ReactiveAuthorityUtils.hasAnyAuthority(
                                        "PERMISSION_USERS.READ", "CLAIM_USERS.READ")
                                .flatMap(hasAuthority -> {
                                    if (hasAuthority) {
                                        return Mono.just(new AuthorizationDecision(true));
                                    }
                                    return ReactiveAuthorityUtils.hasAnyRole("USERS:READ", "ADMIN", "CUSTOMER")
                                            .map(AuthorizationDecision::new);
                                }))

                        // Apply custom authorization logic for the orders path
                        .pathMatchers("/api/{version}/orders/{remainder}")
                        .access((authentication, context) -> ReactiveAuthorityUtils.hasAnyAuthority(
                                        "PERMISSION_ORDERS.READ", "CLAIM_ORDERS.READ")
                                .flatMap(hasAuthority -> {
                                    if (hasAuthority) {
                                        return Mono.just(new AuthorizationDecision(true));
                                    }
                                    return ReactiveAuthorityUtils.hasAnyRole("ORDERS:READ", "ADMIN", "CUSTOMER")
                                            .map(AuthorizationDecision::new);
                                }))

                        // Require authentication for all other requests
                        .anyExchange()
                        .authenticated())
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtFluxAuthenticationConverter())))
                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtFluxAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new CustomFluxJwtAuthenticationConverter());

        return converter;
    }
}
