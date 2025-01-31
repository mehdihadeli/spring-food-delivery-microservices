package com.github.mehdihadeli.buildingblocks.core.data;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.AuditorAware;

// In order for `@CreatedBy` and `@LastModifiedBy` annotations to work, you need to provide an implementation of the
// AuditorAware interface, which is responsible for fetching the current user (or "auditor").
public class AuditorAwareUUID implements AuditorAware<UUID> {
    @Override
    public Optional<UUID> getCurrentAuditor() {
        // TODO: Return the currently `authenticated user`, this could be from a security context
        return Optional.of(UUID.fromString("209A505E-F70B-4E01-8721-49838354F5D9"));
    }
}
