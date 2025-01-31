package com.github.mehdihadeli.buildingblocks.abstractions.core.paging;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IBaseRequest;
import org.springframework.lang.Nullable;

// PageRequest interface
public interface IPageRequest extends IBaseRequest {
    int getPageNumber();

    int getPageSize();

    @Nullable
    String getFilters();

    @Nullable
    String getSortOrder();
}
