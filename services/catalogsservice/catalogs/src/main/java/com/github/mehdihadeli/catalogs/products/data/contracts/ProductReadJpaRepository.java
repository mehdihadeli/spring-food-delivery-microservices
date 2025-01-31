package com.github.mehdihadeli.catalogs.products.data.contracts;

import com.github.mehdihadeli.catalogs.products.data.readentities.ProductReadModel;
import com.github.mehdihadeli.buildingblocks.abstractions.core.data.CustomMongoJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductReadJpaRepository extends CustomMongoJpaRepository<ProductReadModel, UUID> {}
