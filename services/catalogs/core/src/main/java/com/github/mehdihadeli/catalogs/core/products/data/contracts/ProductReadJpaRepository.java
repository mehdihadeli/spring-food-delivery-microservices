package com.github.mehdihadeli.catalogs.core.products.data.contracts;

import com.github.mehdihadeli.buildingblocks.abstractions.core.data.CustomMongoJpaRepository;
import com.github.mehdihadeli.catalogs.core.products.data.readentities.ProductReadModel;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReadJpaRepository extends CustomMongoJpaRepository<ProductReadModel, UUID> {}
