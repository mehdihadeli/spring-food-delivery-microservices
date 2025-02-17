package com.github.mehdihadeli.buildingblocks.security;

import com.github.mehdihadeli.buildingblocks.problemdetails.DefaultProblemDetailsExceptionHandler;
import com.github.mehdihadeli.buildingblocks.problemdetails.ProblemDetailsConfiguration;
import com.github.mehdihadeli.buildingblocks.security.tokenaccessors.OAuthCustomClientTokenAccessor;
import com.github.mehdihadeli.buildingblocks.security.tokenaccessors.OAuthCustomClientTokenAccessorImpl;
import com.github.mehdihadeli.buildingblocks.security.tokenaccessors.OAuthRequestTokenAccessor;
import com.github.mehdihadeli.buildingblocks.security.tokenaccessors.OAuthRequestTokenAccessorImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.client.RestTemplate;

@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(OAthCustomClientOptions.class)
@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, DefaultProblemDetailsExceptionHandler problemDetailsExceptionHandler) throws Exception {
        http
                // Disable CSRF (Cross-Site Request Forgery) protection
                .csrf(AbstractHttpConfigurer::disable)
                // Require authentication for all requests by default
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/app/**", "/webjars/**", "/resources/**", "/css/**")
                            .permitAll();
                    auth.requestMatchers("/swagger/**", "/v3/openapi/**").permitAll();
                    auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll();
                    auth.anyRequest().authenticated();
                })

                // Configure OAuth2 resource server for JWT validation
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwt -> {
                                    // - By default, Spring Security's default JwtAuthenticationConverter onlyextracts
                                    // scopes (e.g., roles, profile, email) from the scope claim in the JWT token. It
                                    // does not automatically extract realm roles or client roles from the realm_access
                                    // and resource_access claims. To fix this, you need to customize the
                                    // JwtAuthenticationConverter to extract roles from the realm_access and
                                    // resource_access claims in the JWT token.
                                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter());
                                })
                                .authenticationEntryPoint(
                                        unauthorizedHandler(problemDetailsExceptionHandler)) // Custom entry point for
                                // unauthenticated requests
                                .accessDeniedHandler(accessDeniedHandler(
                                        problemDetailsExceptionHandler)) // Custom handler for unauthorized requests
                        );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new CustomJwtAuthenticationConverter());

        return converter;
    }

    @Bean
    public KeycloakCustomClientFactory keycloakClientFactory(OAthCustomClientOptions keycloakClientOptions) {
        return new KeycloakCustomClientFactoryImpl(keycloakClientOptions);
    }

    @Bean
    public OAuthCustomClientTokenAccessor customClientTokenAccessor(KeycloakCustomClientFactory keycloakClientFactory) {
        return new OAuthCustomClientTokenAccessorImpl(keycloakClientFactory);
    }

    @Bean
    public OAuthRequestTokenAccessor requestTokenAccessor(
            OAuth2AuthorizedClientService oAuth2AuthorizedClientService, TokenRefresher tokenRefresher) {
        return new OAuthRequestTokenAccessorImpl(oAuth2AuthorizedClientService, tokenRefresher);
    }

    @Bean
    @Qualifier("oauthRequestToken")
    public RestTemplate oauthRequestTokenRestTemplate(OAuthRequestTokenAccessor oAuthRequestTokenAccessor) {
        var restTemplate = new RestTemplate();

        // Add the custom interceptor
        restTemplate.getInterceptors().add(new OAuthRequestTokenInterceptor(oAuthRequestTokenAccessor));

        return restTemplate;
    }

    @Bean
    @Qualifier("oauthCustomClientToken")
    public RestTemplate oauthCustomClientTokenRestTemplate(
            OAuthCustomClientTokenAccessor oauthCustomClientTokenAccessor) {
        RestTemplate restTemplate = new RestTemplate();

        // Add the custom interceptor
        restTemplate.getInterceptors().add(new ClientOAuthTokenInterceptor(oauthCustomClientTokenAccessor));

        return restTemplate;
    }

    @Bean
    OAuthRestTemplate restTemplateWithToken(
            @Qualifier("oauthRequestToken") RestTemplate oauthRequestTokenRestTemplate,
            @Qualifier("oauthCustomClientToken") RestTemplate oauthCustomClientTokenRestTemplate) {
        return new OAuthRestTemplateImpl(oauthCustomClientTokenRestTemplate, oauthRequestTokenRestTemplate);
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new HttpSessionOAuth2AuthorizedClientRepository();
    }

    @Bean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService() {
        return new HttpSessionOAuth2AuthorizedClientService();
    }

    @Bean
    public TokenRefresher tokenRefresher(OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
        return new TokenRefresher(oAuth2AuthorizedClientService);
    }

    @Bean
    public JwtDecoder jwtDecoder(OAuth2ResourceServerProperties properties) {
        return NimbusJwtDecoder.withJwkSetUri(properties.getJwt().getJwkSetUri())
                .build();
    }

    /**
     * Custom AuthenticationEntryPoint to throw an exception for unauthenticated requests.
     */
    private AuthenticationEntryPoint unauthorizedHandler(
            DefaultProblemDetailsExceptionHandler problemDetailsExceptionHandler) {
        return (HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) -> {
            // Throw the exception instead of returning a 401
            ProblemDetailsConfiguration.GlobalExceptionHandler.handleExceptions(
                    problemDetailsExceptionHandler, request, response, authException);
        };
    }

    /**
     * Custom AccessDeniedHandler to throw an exception for unauthorized requests.
     */
    private AccessDeniedHandler accessDeniedHandler(
            DefaultProblemDetailsExceptionHandler problemDetailsExceptionHandler) {
        return (HttpServletRequest request,
                HttpServletResponse response,
                AccessDeniedException accessDeniedException) -> {
            // Throw the exception instead of returning a 403
            ProblemDetailsConfiguration.GlobalExceptionHandler.handleExceptions(
                    problemDetailsExceptionHandler, request, response, accessDeniedException);
        };
    }
}
