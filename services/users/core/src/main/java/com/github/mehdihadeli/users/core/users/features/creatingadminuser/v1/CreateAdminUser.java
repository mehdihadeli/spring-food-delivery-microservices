package com.github.mehdihadeli.users.core.users.features.creatingadminuser.v1;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.ExternalEventBus;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.CommandHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommand;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommandHandler;
import com.github.mehdihadeli.buildingblocks.validation.ValidationUtils;
import com.github.mehdihadeli.buildingblocks.validation.Validator;
import com.github.mehdihadeli.users.core.users.UserMapper;
import com.github.mehdihadeli.users.core.users.contracts.KeycloakUserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

public record CreateAdminUser(
        String userId,
        String userName,
        String password,
        String firstName,
        String lastName,
        String email,
        Map<String, String> clientRoles,
        Map<String, List<String>> attributes)
        implements ICommand<CreateAdminUserResult> {
    public CreateAdminUser {
        if (clientRoles == null) {
            clientRoles = new HashMap<>();
        }
        if (attributes == null) {
            attributes = new HashMap<>();
        }
    }
}

@CommandHandler
class CreateAdminUserHandler implements ICommandHandler<CreateAdminUser, CreateAdminUserResult> {

    private final KeycloakUserService keycloakUserService;
    private final ExternalEventBus externalEventBus;

    public CreateAdminUserHandler(KeycloakUserService keycloakUserService, ExternalEventBus externalEventBus) {
        this.keycloakUserService = keycloakUserService;
        this.externalEventBus = externalEventBus;
    }

    @Override
    public CreateAdminUserResult handle(CreateAdminUser command) throws RuntimeException {
        ValidationUtils.notBeNull(command, "command");

        var createAdminUserDto = UserMapper.createAdminUserDto(command);
        var createUserDto = keycloakUserService.createAdminUser(createAdminUserDto);

        var userCreated = UserMapper.toUserCreatedV1(createUserDto);
        externalEventBus.publish(userCreated);

        return new CreateAdminUserResult(createUserDto.id());
    }
}

@Component
class CreateAdminUserValidator extends Validator<CreateAdminUser> {
    public CreateAdminUserValidator() {
        stringRuleFor(CreateAdminUser::userId, "userId").notEmpty();
        stringRuleFor(CreateAdminUser::userName, "userName").notEmpty();
        stringRuleFor(CreateAdminUser::password, "password").notEmpty();
        stringRuleFor(CreateAdminUser::firstName, "firstName").notEmpty();
        stringRuleFor(CreateAdminUser::lastName, "lastName").notEmpty();
        stringRuleFor(CreateAdminUser::email, "email").notEmpty();
    }
}

record CreateAdminUserResult(String Id) {}
