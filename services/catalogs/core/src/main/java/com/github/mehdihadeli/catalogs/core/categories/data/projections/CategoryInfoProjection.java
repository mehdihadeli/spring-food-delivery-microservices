package com.github.mehdihadeli.catalogs.core.categories.data.projections;

import org.springframework.lang.Nullable;

import java.util.UUID;

public record CategoryInfoProjection(UUID id, String name, String code, @Nullable String description) {}
