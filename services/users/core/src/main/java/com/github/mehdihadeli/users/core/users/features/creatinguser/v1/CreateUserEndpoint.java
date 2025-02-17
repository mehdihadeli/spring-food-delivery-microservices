package com.github.mehdihadeli.users.core.users.features.creatinguser.v1;

import com.github.mehdihadeli.buildingblocks.abstractions.core.id.IdGenerator;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.CommandBus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("api/v1/users")
public class CreateUserEndpoint {
    private final CommandBus commandBus;
    private final IdGenerator idGenerator;

    public CreateUserEndpoint(CommandBus commandBus, IdGenerator idGenerator) {
        this.commandBus = commandBus;
        this.idGenerator = idGenerator;
    }

    @PreAuthorize(
            "hasAnyAuthority('PERMISSION_USERS.WRITE') or hasAnyAuthority('CLAIM_USERS.WRITE') or  hasAnyRole('USERS:WRITE','ADMIN')")
    @Operation(summary = "Create user", description = "Create user", operationId = "CreateUser")
    @Tag(name = "Users")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(
            responseCode = "401",
            description = "UnAuthorize",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    // https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/responsebody.html
    ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) throws URISyntaxException {
        var userId = this.idGenerator.generateId();

        // https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responseentity.html
        try (MDC.MDCCloseable md = MDC.putCloseable("userId", userId.toString())) {

            var createUser = new CreateUser(
                    userId.toString(),
                    request.userName(),
                    request.password(),
                    request.firstName(),
                    request.lastName(),
                    request.email(),
                    request.emailVerified(),
                    request.enabled(),
                    request.roles(),
                    request.clientRoles(),
                    request.attributes());

            var result = commandBus.send(createUser);
            URI location = new URI(String.format("/api/v1/users/%s", result.Id()));

            return ResponseEntity.created(location).body(new CreateUserResponse(result.Id()));
        }
    }
}

record CreateUserRequest(
        String userName,
        String password,
        String firstName,
        String lastName,
        String email,
        boolean emailVerified,
        boolean enabled,
        List<String> roles,
        Map<String, String> clientRoles,
        Map<String, List<String>> attributes) {}

record CreateUserResponse(String Id) {}
