package com.github.mehdihadeli.apigateway.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Set;

public final class ReactiveAuthorityUtils {

    private static final String DEFAULT_ROLE_PREFIX = "ROLE_";

    private ReactiveAuthorityUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Checks if the current user has the specified authority.
     */
    public static Mono<Boolean> hasAuthority(String authority) {
        return hasAnyAuthority(authority);
    }

    /**
     * Checks if the current user has any of the specified authorities.
     */
    public static Mono<Boolean> hasAnyAuthority(String... authorities) {
        return hasAnyAuthorityName(null, authorities);
    }

    /**
     * Checks if the current user has the specified role.
     */
    public static Mono<Boolean> hasRole(String role) {
        return hasAnyRole(role);
    }

    /**
     * Checks if the current user has any of the specified roles.
     */
    public static Mono<Boolean> hasAnyRole(String... roles) {
        return hasAnyAuthorityName(DEFAULT_ROLE_PREFIX, roles);
    }

    /**
     * Helper method to check if the user has any of the specified authorities or roles.
     */
    private static Mono<Boolean> hasAnyAuthorityName(String prefix, String... roles) {
        return getAuthoritySet().map(authorities -> {
            for (String role : roles) {
                String defaultedRole = getRoleWithDefaultPrefix(prefix, role);
                if (authorities.contains(defaultedRole)) {
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * Retrieves the set of authorities for the current user.
     */
    private static Mono<Set<String>> getAuthoritySet() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    if (authentication instanceof JwtAuthenticationToken) {
                        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
                        Collection<? extends GrantedAuthority> authorities = jwtAuth.getAuthorities();
                        return Mono.just(AuthorityUtils.authorityListToSet(authorities));
                    }
                    return Mono.just(Set.of()); // No authorities if not a JWT token
                });
    }

    /**
     * Adds the default role prefix to a role if necessary.
     */
    private static String getRoleWithDefaultPrefix(String prefix, String role) {
        if (role == null) {
            return role;
        } else if (prefix != null && !role.startsWith(prefix)) {
            return prefix + role;
        }
        return role;
    }
}
