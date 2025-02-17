package com.github.mehdihadeli.shared.users.users.events.integration.v1;

import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IIntegrationEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeEmpty;
import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

public record UserCreatedV1(
        UUID messageId,
        String userId,
        String username,
        String firstName,
        String lastName,
        String email,
        boolean emailVerified,
        boolean enabled,
        List<String> roles,
        Map<String, String> clientRoles,
        Map<String, List<String>> attributes,
        LocalDateTime created)
        implements IIntegrationEvent {
    public UserCreatedV1 {
        notBeNull(messageId, "messageId");
        notBeEmpty(userId, "userId");
        notBeEmpty(username, "username");
        notBeEmpty(firstName, "firstName");
        notBeEmpty(lastName, "lastName");
        notBeNull(created, "created");
    }
}
