package com.github.mehdihadeli.users.core.users.services;

import com.github.mehdihadeli.buildingblocks.security.KeycloakCustomClientFactory;
import com.github.mehdihadeli.buildingblocks.security.OAthCustomClientOptions;
import com.github.mehdihadeli.users.core.Constants;
import com.github.mehdihadeli.users.core.users.contracts.KeycloakUserService;
import com.github.mehdihadeli.users.core.users.dtos.CreateAdminUserDto;
import com.github.mehdihadeli.users.core.users.dtos.CreateCustomerUserDto;
import com.github.mehdihadeli.users.core.users.dtos.CreateUserDto;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

public class KeycloakUserServiceImpl implements KeycloakUserService {
    private final RealmResource realmResource;

    public KeycloakUserServiceImpl(
            KeycloakCustomClientFactory keycloakCustomClientFactory, OAthCustomClientOptions oathCustomClientOptions) {
        var keycloakCustomClientFactoryClient = keycloakCustomClientFactory.createClient();
        // realm name for creating new users, with using our admin realm
        realmResource = keycloakCustomClientFactoryClient.realm(oathCustomClientOptions.getApplicationRealmName());
    }

    @Override
    public void createUser(CreateUserDto createUserDto) {
        UsersResource usersResource = this.realmResource.users();

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(createUserDto.username());
        userRepresentation.setId(createUserDto.id().toString());
        userRepresentation.setEmail(createUserDto.email());
        userRepresentation.setFirstName(createUserDto.firstName());
        userRepresentation.setLastName(createUserDto.lastName());
        userRepresentation.setEnabled(createUserDto.enabled());
        userRepresentation.setEmailVerified(createUserDto.emailVerified());

        // Set attributes (if any)
        if (createUserDto.attributes() != null && !createUserDto.attributes().isEmpty()) {
            userRepresentation.setAttributes(createUserDto.attributes());
        }

        // Create the user
        usersResource.create(userRepresentation);

        // Set the user's password
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(createUserDto.password());
        credential.setTemporary(false); // Password is not temporary

        // Get the user ID
        String userId = usersResource.search(createUserDto.username()).get(0).getId();

        // Set the password
        usersResource.get(userId).resetPassword(credential);

        // Assign roles to the user
        if (createUserDto.roles() != null && !createUserDto.roles().isEmpty()) {
            UserResource userResource = usersResource.get(userId);
            for (String roleName : createUserDto.roles()) {
                RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
                userResource.roles().realmLevel().add(Collections.singletonList(role));
            }
        }
    }

    @Override
    public CreateUserDto createCustomerUser(CreateCustomerUserDto createCustomerUserDto) {
        var createUser = new CreateUserDto(
                createCustomerUserDto.id(),
                createCustomerUserDto.username(),
                createCustomerUserDto.password(),
                createCustomerUserDto.firstName(),
                createCustomerUserDto.lastName(),
                createCustomerUserDto.email(),
                true,
                true,
                List.of(Constants.Roles.CUSTOMER),
                new HashMap<>(),
                createCustomerUserDto.attributes());

        createUser(createUser);

        return createUser;
    }

    @Override
    public CreateUserDto createAdminUser(CreateAdminUserDto createAdminUserDto) {
        var createUser = new CreateUserDto(
                createAdminUserDto.id(),
                createAdminUserDto.username(),
                createAdminUserDto.password(),
                createAdminUserDto.firstName(),
                createAdminUserDto.lastName(),
                createAdminUserDto.email(),
                createAdminUserDto.emailVerified(),
                createAdminUserDto.enabled(),
                List.of(Constants.Roles.ADMIN),
                createAdminUserDto.clientRoles(),
                createAdminUserDto.attributes());

        createUser(createUser);

        return createUser;
    }
}
