package com.github.mehdihadeli.buildingblocks.abstractions.core.data;

import java.io.Serializable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

// https://docs.spring.io/spring-data/mongodb/reference/repositories/definition.html
// https://docs.spring.io/spring-data/mongodb/reference/mongodb/repositories/repositories.html

// `NoRepositoryBean` This prevents Spring Data to try to create an instance of it directly and failing because it can’t
// determine the entity for that repository, since it still contains a generic type variable.  It is typically applied
// to base repository interfaces that are meant to be extended by other repository interfaces but should not be
// instantiated

// we can't use JpaRepository for MongoDB in a Spring application. JpaRepository is specifically designed for relational
// databases. The equivalent of JpaRepository in Spring Data MongoDB is MongoRepository and implements
// `SimpleMongoRepository`
@NoRepositoryBean
public interface CustomMongoJpaRepository<TReadEntity, TID extends Serializable>
        extends MongoRepository<TReadEntity, TID>, ProjectionRepository
// TODO: fix issue in generating Q prefix readmodel for querydsl
// ,QuerydslPredicateExecutor<TReadEntity>
{}
