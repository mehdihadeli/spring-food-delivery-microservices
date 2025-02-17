package com.github.mehdihadeli.users.core.users.contracts;

import com.github.mehdihadeli.users.core.users.dtos.CreateAdminUserDto;
import com.github.mehdihadeli.users.core.users.dtos.CreateCustomerUserDto;
import com.github.mehdihadeli.users.core.users.dtos.CreateUserDto;

public interface KeycloakUserService {
    void createUser(CreateUserDto createUserDto);

    CreateUserDto createCustomerUser(CreateCustomerUserDto createCustomerUserDto);

    CreateUserDto createAdminUser(CreateAdminUserDto createAdminUserDto);
}
