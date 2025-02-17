package com.github.mehdihadeli.users.core.users.models;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record User(
        UUID id,
        String username,
        String firstName,
        String lastName,
        String email,
        boolean emailVerified,
        boolean enabled,
        List<String> roles,
        Map<String, String> clientRoles,
        Map<String, List<String>> attributes) {}
