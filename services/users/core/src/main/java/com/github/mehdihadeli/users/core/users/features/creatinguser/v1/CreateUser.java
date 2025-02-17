package com.github.mehdihadeli.users.core.users.features.creatinguser.v1;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.ExternalEventBus;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.CommandHandler;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommand;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommandHandler;
import com.github.mehdihadeli.buildingblocks.validation.ValidationUtils;
import com.github.mehdihadeli.buildingblocks.validation.Validator;
import com.github.mehdihadeli.users.core.users.UserMapper;
import com.github.mehdihadeli.users.core.users.contracts.KeycloakUserService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record CreateUser(
        String userId,
        String userName,
        String password,
        String firstName,
        String lastName,
        String email,
        boolean emailVerified,
        boolean enabled,
        List<String> roles,
        Map<String, String> clientRoles,
        Map<String, List<String>> attributes)
        implements ICommand<CreateUserResult> {
    public CreateUser {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        if (clientRoles == null) {
            clientRoles = new HashMap<>();
        }
        if (attributes == null) {
            attributes = new HashMap<>();
        }
    }
}

@CommandHandler
class CreateUserHandler implements ICommandHandler<CreateUser, CreateUserResult> {

    private final KeycloakUserService keycloakUserService;
    private final ExternalEventBus externalEventBus;

    public CreateUserHandler(KeycloakUserService keycloakUserService, ExternalEventBus externalEventBus) {
        this.keycloakUserService = keycloakUserService;
        this.externalEventBus = externalEventBus;
    }

    @Override
    public CreateUserResult handle(CreateUser command) throws RuntimeException {
        ValidationUtils.notBeNull(command, "command");

        var createUserDto = UserMapper.toCreateUserDto(command);
        keycloakUserService.createUser(createUserDto);

        var userCreated = UserMapper.toUserCreatedV1(createUserDto);
        externalEventBus.publish(userCreated);

        return new CreateUserResult(createUserDto.id());
    }
}

@Component
class CreateUserValidator extends Validator<CreateUser> {
    public CreateUserValidator() {
        stringRuleFor(CreateUser::userId, "userId").notEmpty();
        stringRuleFor(CreateUser::userName, "userName").notEmpty();
        stringRuleFor(CreateUser::password, "password").notEmpty();
        stringRuleFor(CreateUser::firstName, "firstName").notEmpty();
        stringRuleFor(CreateUser::lastName, "lastName").notEmpty();
        stringRuleFor(CreateUser::email, "email").notEmpty();
    }
}

record CreateUserResult(String Id) {}
