package com.github.mehdihadeli.apigateway.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomFluxJwtAuthenticationConverter implements Converter<Jwt, Flux<GrantedAuthority>> {

    private final JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public Flux<GrantedAuthority> convert(Jwt jwt) {
        // Start with default authorities
        Collection<GrantedAuthority> authorities = new HashSet<>(defaultConverter.convert(jwt));

        // Extract and add realm roles
        authorities.addAll(extractRealmRoles(jwt));

        // Extract and add client roles
        authorities.addAll(extractClientRoles(jwt));

        // Extract and add scopes as authorities
        authorities.addAll(extractScopes(jwt));

        // Extract and add permissions from 'access'
        authorities.addAll(extractAccessPermissions(jwt));

        // Extract token metadata
        authorities.addAll(extractTokenMetadata(jwt));

        // Extract additional claims
        authorities.addAll(extractAdditionalClaims(jwt));

        // Convert the collection to a Flux
        return Flux.fromIterable(authorities);
    }

    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            return ((List<String>) realmAccess.get("roles"))
                    .stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                            .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    private Collection<GrantedAuthority> extractClientRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null) {
            return resourceAccess.entrySet().stream()
                    .flatMap(entry -> {
                        String client = entry.getKey();
                        Map<String, Object> clientRoles = (Map<String, Object>) entry.getValue();
                        if (clientRoles.containsKey("roles")) {
                            return ((List<String>) clientRoles.get("roles"))
                                    .stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
                        }
                        return Stream.empty();
                    })
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    private Collection<GrantedAuthority> extractScopes(Jwt jwt) {
        String scope = jwt.getClaim("scope");
        if (scope != null) {
            return Arrays.stream(scope.split(" "))
                    .map(s -> new SimpleGrantedAuthority("SCOPE_" + s))
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    private Collection<GrantedAuthority> extractAccessPermissions(Jwt jwt) {
        Map<String, Object> access = jwt.getClaim("access");
        if (access != null) {
            return access.entrySet().stream()
                    .flatMap(entry -> {
                        Object permissions = entry.getValue();
                        if (permissions instanceof List) {
                            return ((List<String>) permissions)
                                    .stream()
                                            .map(perm ->
                                                    new SimpleGrantedAuthority("PERMISSION_" + perm.toUpperCase()));
                        } else if (permissions instanceof String) {
                            return Stream.of(new SimpleGrantedAuthority(
                                    "PERMISSION_" + permissions.toString().toUpperCase()));
                        }
                        return Stream.empty();
                    })
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    private Collection<GrantedAuthority> extractTokenMetadata(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Extract token-related metadata
        authorities.add(new SimpleGrantedAuthority("TOKEN_ID_" + jwt.getClaim("jti")));
        authorities.add(new SimpleGrantedAuthority("TOKEN_ISSUER_" + jwt.getClaim("iss")));
        authorities.add(new SimpleGrantedAuthority("TOKEN_SUBJECT_" + jwt.getClaim("sub")));
        authorities.add(new SimpleGrantedAuthority("TOKEN_EXPIRATION_" + jwt.getClaim("exp")));
        authorities.add(new SimpleGrantedAuthority("TOKEN_ISSUED_AT_" + jwt.getClaim("iat")));

        // Extract audiences (multiple possible)
        List<String> audiences = jwt.getClaimAsStringList("aud");
        if (audiences != null) {
            audiences.forEach(
                    aud -> authorities.add(new SimpleGrantedAuthority("TOKEN_AUDIENCE_" + aud.toUpperCase())));
        }

        return authorities;
    }

    private Collection<GrantedAuthority> extractAdditionalClaims(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // List of additional claims to be extracted
        List<String> additionalClaims =
                List.of("client_id", "service_name", "clientHost", "clientAddress", "preferred_username");

        for (String claimKey : additionalClaims) {
            String claimValue = jwt.getClaim(claimKey);
            if (claimValue != null) {
                authorities.add(
                        new SimpleGrantedAuthority("CLAIM_" + claimKey.toUpperCase() + "_" + claimValue.toUpperCase()));
            }
        }

        authorities.addAll(extractAccessClaims(jwt));

        return authorities;
    }

    private Collection<GrantedAuthority> extractAccessClaims(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        Map<String, Object> access = jwt.getClaim("access");

        if (access != null) {
            for (Map.Entry<String, Object> entry : access.entrySet()) {
                if (entry.getValue() instanceof List) {
                    ((List<String>) entry.getValue())
                            .forEach(value ->
                                    authorities.add(new SimpleGrantedAuthority("CLAIM_" + value.toUpperCase())));
                } else if (entry.getValue() instanceof String) {
                    authorities.add(new SimpleGrantedAuthority(
                            "CLAIM_" + entry.getValue().toString().toUpperCase()));
                }
            }
        }
        return authorities;
    }
}
