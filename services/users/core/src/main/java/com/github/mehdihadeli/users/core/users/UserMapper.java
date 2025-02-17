package com.github.mehdihadeli.users.core.users;

import com.github.mehdihadeli.buildingblocks.core.messaging.MessageUtils;
import com.github.mehdihadeli.shared.users.users.events.integration.v1.UserCreatedV1;
import com.github.mehdihadeli.users.core.users.dtos.CreateAdminUserDto;
import com.github.mehdihadeli.users.core.users.dtos.CreateCustomerUserDto;
import com.github.mehdihadeli.users.core.users.dtos.CreateUserDto;
import com.github.mehdihadeli.users.core.users.features.creatingadminuser.v1.CreateAdminUser;
import com.github.mehdihadeli.users.core.users.features.creatingcustomeruser.v1.CreateCustomerUser;
import com.github.mehdihadeli.users.core.users.features.creatinguser.v1.CreateUser;

import java.time.LocalDateTime;

public final class UserMapper {
    private UserMapper() {}

    public static CreateUserDto toCreateUserDto(CreateUser createUser) {
        return new CreateUserDto(
                createUser.userId(),
                createUser.userName(),
                createUser.password(),
                createUser.firstName(),
                createUser.lastName(),
                createUser.email(),
                createUser.emailVerified(),
                createUser.enabled(),
                createUser.roles(),
                createUser.clientRoles(),
                createUser.attributes());
    }

    public static CreateCustomerUserDto createCustomerUserDto(CreateCustomerUser createCustomer) {
        return new CreateCustomerUserDto(
                createCustomer.userId(),
                createCustomer.userName(),
                createCustomer.password(),
                createCustomer.firstName(),
                createCustomer.lastName(),
                createCustomer.email(),
                createCustomer.attributes());
    }

    public static CreateAdminUserDto createAdminUserDto(CreateAdminUser createAdminUser) {
        return new CreateAdminUserDto(
                createAdminUser.userId(),
                createAdminUser.userName(),
                createAdminUser.password(),
                createAdminUser.firstName(),
                createAdminUser.lastName(),
                createAdminUser.email(),
                true,
                true,
                createAdminUser.clientRoles(),
                createAdminUser.attributes());
    }

    public static UserCreatedV1 toUserCreatedV1(CreateUserDto createUserDto) {
        return new UserCreatedV1(
                MessageUtils.generateMessageId(),
                createUserDto.id(),
                createUserDto.username(),
                createUserDto.firstName(),
                createUserDto.lastName(),
                createUserDto.email(),
                createUserDto.emailVerified(),
                createUserDto.enabled(),
                createUserDto.roles(),
                createUserDto.clientRoles(),
                createUserDto.attributes(),
                LocalDateTime.now());
    }
}
