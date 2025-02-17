package com.github.mehdihadeli.users.core.users.dtos;

import java.util.List;
import java.util.Map;

public record CreateAdminUserDto(
        String id,
        String username,
        String password,
        String firstName,
        String lastName,
        String email,
        boolean emailVerified,
        boolean enabled,
        Map<String, String> clientRoles,
        Map<String, List<String>> attributes) {}
