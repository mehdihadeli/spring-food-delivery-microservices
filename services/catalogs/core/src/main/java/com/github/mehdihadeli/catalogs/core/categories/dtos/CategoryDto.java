package com.github.mehdihadeli.catalogs.core.categories.dtos;

import java.util.UUID;
import org.springframework.lang.Nullable;

public record CategoryDto(UUID id, String name, String code, @Nullable String description) {}
