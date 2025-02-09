package com.github.mehdihadeli.catalogs.api.unittests.products.features.creatingproduct.v1;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.mehdihadeli.buildingblocks.core.exceptions.ConflictException;
import com.github.mehdihadeli.buildingblocks.core.exceptions.ValidationException;
import com.github.mehdihadeli.buildingblocks.test.UnitTestBase;
import com.github.mehdihadeli.catalogs.api.fakes.products.FakeCreateProductV1;
import com.github.mehdihadeli.catalogs.core.products.ProductMapper;
import com.github.mehdihadeli.catalogs.core.products.data.contracts.ProductAggregateRepository;
import com.github.mehdihadeli.catalogs.core.products.domain.models.entities.Product;
import com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.CreateProduct;
import com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.CreateProductHandler;
import com.github.mehdihadeli.catalogs.core.products.features.creatingproduct.v1.CreateProductResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class CreateProductTests extends UnitTestBase {

    @Mock
    private ProductAggregateRepository productAggregateRepository;

    @InjectMocks
    private CreateProductHandler createProductHandler;

    private CreateProduct createProduct;

    @BeforeEach
    void setUp() {
        createProduct = FakeCreateProductV1.generate();
    }

    @Test
    void can_create_product_with_valid_inputs() {
        // Arrange
        Product product = ProductMapper.toProductAggregate(createProduct);
        when(productAggregateRepository.add(any(Product.class))).thenReturn(product);

        // Act
        CreateProductResult result = createProductHandler.handle(createProduct);

        // Assert
        assertNotNull(result);
        assertEquals(createProduct.productId().id(), result.Id());
        verify(productAggregateRepository, times(1)).add(any(Product.class));
    }

    @Test
    void must_throw_validation_exception_exception_with_null_command() {
        // Act & Assert
        ValidationException exception =
                assertThrows(ValidationException.class, () -> createProductHandler.handle(null));

        // Verify the exception message
        assertEquals("command cannot be null.", exception.getMessage());
    }

    @Test
    void must_throw_validation_exception_exception_with_invalid_command_productId() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> new CreateProduct(
                        null, // productId is null
                        createProduct.categoryId(),
                        createProduct.name(),
                        createProduct.price(),
                        createProduct.status(),
                        createProduct.dimensions(),
                        createProduct.size(),
                        createProduct.initialVariants(),
                        createProduct.description()));

        assertEquals("productId cannot be null.", exception.getMessage());
    }

    @Test
    void must_throw_conflict_exception_exception_when_product_already_exists() {
        // Arrange
        Product product = ProductMapper.toProductAggregate(createProduct);
        when(productAggregateRepository.add(any(Product.class)))
                .thenThrow(new ConflictException("Entity with ID " + product.getId() + " already exists."));

        // Act & Assert
        ConflictException exception =
                assertThrows(ConflictException.class, () -> createProductHandler.handle(createProduct));

        // Verify the exception message
        assertEquals("Entity with ID " + product.getId() + " already exists.", exception.getMessage());
    }
}
