package com.github.mehdihadeli.users.core.users.features.creatingcustomeruser.v1;

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

public record CreateCustomerUser(
        String userId,
        String userName,
        String password,
        String firstName,
        String lastName,
        String email,
        Map<String, List<String>> attributes)
        implements ICommand<CreateCustomerUserResult> {
    public CreateCustomerUser {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
    }
}

@CommandHandler
class CreateCustomerUserHandler implements ICommandHandler<CreateCustomerUser, CreateCustomerUserResult> {

    private final KeycloakUserService keycloakUserService;
    private final ExternalEventBus externalEventBus;

    public CreateCustomerUserHandler(KeycloakUserService keycloakUserService, ExternalEventBus externalEventBus) {
        this.keycloakUserService = keycloakUserService;
        this.externalEventBus = externalEventBus;
    }

    @Override
    public CreateCustomerUserResult handle(CreateCustomerUser command) throws RuntimeException {
        ValidationUtils.notBeNull(command, "command");

        var createCustomerUserDto = UserMapper.createCustomerUserDto(command);
        var createUserDto = keycloakUserService.createCustomerUser(createCustomerUserDto);

        var userCreated = UserMapper.toUserCreatedV1(createUserDto);
        externalEventBus.publish(userCreated);

        return new CreateCustomerUserResult(createUserDto.id());
    }
}

@Component
class CreateCustomerUserValidator extends Validator<CreateCustomerUser> {
    public CreateCustomerUserValidator() {
        stringRuleFor(CreateCustomerUser::userId, "userId").notEmpty();
        stringRuleFor(CreateCustomerUser::userName, "userName").notEmpty();
        stringRuleFor(CreateCustomerUser::password, "password").notEmpty();
        stringRuleFor(CreateCustomerUser::firstName, "firstName").notEmpty();
        stringRuleFor(CreateCustomerUser::lastName, "lastName").notEmpty();
        stringRuleFor(CreateCustomerUser::email, "email").notEmpty();
    }
}

record CreateCustomerUserResult(String Id) {}
