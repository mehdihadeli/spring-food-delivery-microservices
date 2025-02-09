package com.github.mehdihadeli.catalogs.api.endtoendtests.products.features.creatingproduct;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import com.github.mehdihadeli.buildingblocks.test.EndToEndTestBase;
import com.github.mehdihadeli.catalogs.api.fakes.products.FakeCreateProductRequestV1;
import com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.CreateProductResponse;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;

public class CreateProductTests extends EndToEndTestBase {
    public CreateProductTests(ApplicationContext applicationContext) {
        super(applicationContext, "api/v1/products");
    }

    @Test
    public void can_returns_created_status_code_using_valid_dto_and_auth_credentials() {
        var request = FakeCreateProductRequestV1.generate();

        CreateProductResponse response = given().auth()
                .basic("test", "test")
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(getTestApiUrl())
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .extract()
                .as(CreateProductResponse.class);

        assertThat(response.id()).isNotNull();
    }
}
