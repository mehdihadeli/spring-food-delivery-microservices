package com.github.mehdihadeli.buildingblocks.core.data;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;

// In order for `@CreatedBy` and `@LastModifiedBy` annotations to work, you need to provide an implementation of the
// AuditorAware interface, which is responsible for fetching the current user (or "auditor").
public class AuditorAwareLong implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        // TODO: Return the currently `authenticated user`, this could be from a security context
        return Optional.of((long) 1);
    }
}
