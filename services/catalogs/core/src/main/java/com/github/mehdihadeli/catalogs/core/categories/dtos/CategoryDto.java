package com.github.mehdihadeli.catalogs.core.categories.dtos;

import org.springframework.lang.Nullable;

import java.util.UUID;

public record CategoryDto(UUID id, String name, String code, @Nullable String description) {}
