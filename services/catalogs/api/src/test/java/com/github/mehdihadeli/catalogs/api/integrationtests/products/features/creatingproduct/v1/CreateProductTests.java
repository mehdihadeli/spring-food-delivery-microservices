package com.github.mehdihadeli.catalogs.api.integrationtests.products.features.creatingproduct.v1;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.github.mehdihadeli.buildingblocks.core.data.EntityManagerUtils;
import com.github.mehdihadeli.buildingblocks.test.IntegrationTestBase;
import com.github.mehdihadeli.catalogs.api.fakes.products.FakeCreateProductV1;
import com.github.mehdihadeli.catalogs.core.products.data.entities.ProductDataModel;
import com.github.mehdihadeli.catalogs.core.products.data.entities.ProductDataModel_;
import com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.events.internal.mongo.CreateProductRead;
import com.github.mehdihadeli.shared.catalogs.products.events.integration.v1.ProductCreatedV1;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

public class CreateProductTests extends IntegrationTestBase {
    protected CreateProductTests(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Test
    public void can_create_new_product_with_valid_input_in_postgres_db() {
        // Arrange
        var command = FakeCreateProductV1.generate();

        // Act
        var result = this.sharedFixture().sendCommand(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.Id()).isEqualTo(command.productId().id());

        var createdProduct = sharedFixture().executeEntityManager((e, m) -> {
            return EntityManagerUtils.findOne(
                            e,
                            ProductDataModel.class,
                            (root, cb) -> cb.equal(root.get(ProductDataModel_.id), result.Id()))
                    .orElse(null);
        });
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getId()).isEqualTo(command.productId().id());
    }

    @Test
    public void can_save_mongo_product_read_model_in_internal_persistence_message() {
        // Arrange
        var command = FakeCreateProductV1.generate();

        // Act
        this.sharedFixture().sendCommand(command);

        // Assert
        sharedFixture().shouldProcessingInternalCommand(CreateProductRead.class);
    }

    @Test
    public void can_publish_product_created_integration_event_to_the_broker() {
        // Arrange
        var command = FakeCreateProductV1.generate();

        // Act
        this.sharedFixture().sendCommand(command);

        // Assert
        sharedFixture().shouldPublishing(ProductCreatedV1.class);
    }

    @Test
    public void can_save_product_created_integration_event_in_the_outbox() {
        // Arrange
        var command = FakeCreateProductV1.generate();

        // Act
        this.sharedFixture().sendCommand(command);

        // Assert
        sharedFixture().shouldProcessingOutboxMessage(ProductCreatedV1.class);
    }
}
