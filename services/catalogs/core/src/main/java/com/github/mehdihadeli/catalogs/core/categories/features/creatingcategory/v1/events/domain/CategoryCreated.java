package com.github.mehdihadeli.catalogs.core.categories.features.creatingcategory.v1.events.domain;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEventHandler;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.CommandBus;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Code;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Description;
import com.github.mehdihadeli.buildingblocks.core.data.valueobjects.Name;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.notifications.NotificationHandler;
import com.github.mehdihadeli.catalogs.core.categories.models.valueobjects.CategoryId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

// https://event-driven.io/en/explicit_validation_in_csharp_just_got_simpler/
// https://event-driven.io/en/how_to_validate_business_logic/
// https://event-driven.io/en/notes_about_csharp_records_and_nullable_reference_types/
// https://buildplease.com/pages/vos-in-events/
// https://codeopinion.com/leaking-value-objects-from-your-domain/
// https://www.youtube.com/watch?v=CdanF8PWJng
// if we want to store domain events in eventstoredb we should use primitive types instead of value obejcts and entities
// because with changing in rules and filed in vo our old events can't restore successfully.
public record CategoryCreated(CategoryId categoryId, Name name, Code code, @Nullable Description description)
        implements IDomainEvent {}

@NotificationHandler
class CategoryCreatedDomainHandler implements IDomainEventHandler<CategoryCreated> {
    private static final Logger logger = LoggerFactory.getLogger(CategoryCreatedDomainHandler.class);

    private final CommandBus commandBus;

    CategoryCreatedDomainHandler(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @Override
    public void handle(CategoryCreated categoryCreated) throws RuntimeException {
        logger.atInfo()
                .addKeyValue("domain-event", categoryCreated)
                .log("domain event {} handled.", categoryCreated.getClass().getSimpleName());
    }
}
