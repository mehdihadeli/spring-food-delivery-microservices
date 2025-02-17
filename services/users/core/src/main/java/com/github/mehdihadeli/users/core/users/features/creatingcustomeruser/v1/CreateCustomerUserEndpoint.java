package com.github.mehdihadeli.users.core.users.features.creatingcustomeruser.v1;

import com.github.mehdihadeli.buildingblocks.abstractions.core.id.IdGenerator;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.CommandBus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("api/v1/users")
public class CreateCustomerUserEndpoint {
    private final CommandBus commandBus;
    private final IdGenerator idGenerator;

    public CreateCustomerUserEndpoint(CommandBus commandBus, IdGenerator idGenerator) {
        this.commandBus = commandBus;
        this.idGenerator = idGenerator;
    }

    @Operation(summary = "Create customer", description = "Create customer", operationId = "CreateCustomer")
    @Tag(name = "Users")
    @ApiResponse(responseCode = "201", description = "Created")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("customer-user")
    // https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/responsebody.html
    ResponseEntity<CreateCustomerUserResponse> createCustomer(@RequestBody CreateCustomerUserRequest request)
            throws URISyntaxException {
        var userId = this.idGenerator.generateId();

        // https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responseentity.html
        try (MDC.MDCCloseable md = MDC.putCloseable("userId", userId.toString())) {

            var createUser = new CreateCustomerUser(
                    userId.toString(),
                    request.userName(),
                    request.password(),
                    request.firstName(),
                    request.lastName(),
                    request.email(),
                    request.attributes());

            var result = commandBus.send(createUser);
            URI location = new URI(String.format("/api/v1/users/%s", result.Id()));

            return ResponseEntity.created(location).body(new CreateCustomerUserResponse(result.Id()));
        }
    }
}

record CreateCustomerUserRequest(
        String userName,
        String password,
        String firstName,
        String lastName,
        String email,
        Map<String, List<String>> attributes) {}

record CreateCustomerUserResponse(String Id) {}
