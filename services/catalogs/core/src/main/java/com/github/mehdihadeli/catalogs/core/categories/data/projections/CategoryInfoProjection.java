package com.github.mehdihadeli.catalogs.core.categories.data.projections;

import java.util.UUID;
import org.springframework.lang.Nullable;

public record CategoryInfoProjection(UUID id, String name, String code, @Nullable String description) {}
