package com.github.mehdihadeli.catalogs.products.data.readentities;

import com.github.mehdihadeli.buildingblocks.core.data.AuditableReadEntity;
import com.github.mehdihadeli.catalogs.products.data.valueobjects.MoneyVO;
import java.util.UUID;

public class ProductVariantReadModel extends AuditableReadEntity {
    private UUID productId;
    private String sku;
    private MoneyVO price;
    private Integer stock;
    private String color;

    // Constructor for mapping from ProductVariantDataModel
    public ProductVariantReadModel(UUID id, UUID productId, String sku, MoneyVO price, Integer stock, String color) {
        this.setId(id);
        this.productId = productId;
        this.sku = sku;
        this.price = price;
        this.stock = stock;
        this.color = color;
    }

    // Private default constructor for jpa mongo and Jackson serializer
    private ProductVariantReadModel() {}

    // Getters and Setters
    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public MoneyVO getPrice() {
        return price;
    }

    public void setPrice(MoneyVO price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }
}
