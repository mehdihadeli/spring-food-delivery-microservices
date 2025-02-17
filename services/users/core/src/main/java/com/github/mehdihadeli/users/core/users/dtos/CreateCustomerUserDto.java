package com.github.mehdihadeli.users.core.users.dtos;

import java.util.List;
import java.util.Map;

public record CreateCustomerUserDto(
        String id,
        String username,
        String password,
        String firstName,
        String lastName,
        String email,
        Map<String, List<String>> attributes) {}
