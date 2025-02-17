package com.github.mehdihadeli.users.core.users.features.creatingadminuser.v1;

import com.github.mehdihadeli.buildingblocks.abstractions.core.id.IdGenerator;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.CommandBus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("api/v1/users")
public class CreateAdminUserEndpoint {
    private final CommandBus commandBus;
    private final IdGenerator idGenerator;

    public CreateAdminUserEndpoint(CommandBus commandBus, IdGenerator idGenerator) {
        this.commandBus = commandBus;
        this.idGenerator = idGenerator;
    }

    @PreAuthorize(
            "hasAnyAuthority('PERMISSION_USERS.WRITE') or hasAnyAuthority('CLAIM_USERS.WRITE') or  hasAnyRole('USERS:WRITE','ADMIN')")
    @Operation(summary = "Create admin user", description = "Create admin user", operationId = "CreateAdminUser")
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
    @PostMapping("admin-user")
    // https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/responsebody.html
    ResponseEntity<CreateAdminUserResponse> createProduct(@RequestBody CreateAdminUserRequest request)
            throws URISyntaxException {
        var userId = this.idGenerator.generateId();

        // https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responseentity.html
        try (MDC.MDCCloseable md = MDC.putCloseable("userId", userId.toString())) {

            var createAdminUser = new CreateAdminUser(
                    userId.toString(),
                    request.userName(),
                    request.password(),
                    request.firstName(),
                    request.lastName(),
                    request.email(),
                    request.clientRoles(),
                    request.attributes());

            var result = commandBus.send(createAdminUser);
            URI location = new URI(String.format("/api/v1/users/%s", result.Id()));

            return ResponseEntity.created(location).body(new CreateAdminUserResponse(result.Id()));
        }
    }
}

record CreateAdminUserRequest(
        String userName,
        String password,
        String firstName,
        String lastName,
        String email,
        Map<String, String> clientRoles,
        Map<String, List<String>> attributes) {}

record CreateAdminUserResponse(String Id) {}
