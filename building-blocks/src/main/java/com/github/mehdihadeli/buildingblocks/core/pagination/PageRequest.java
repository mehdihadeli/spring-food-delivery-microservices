package com.github.mehdihadeli.buildingblocks.core.pagination;

import com.github.mehdihadeli.buildingblocks.abstractions.core.paging.IPageRequest;
import org.springframework.lang.Nullable;

public class PageRequest implements IPageRequest {
    private final int pageNumber;
    private final int pageSize;

    @Nullable
    private final String filters;

    @Nullable
    private final String sortOrder;

    public PageRequest(int pageNumber, int pageSize, @Nullable String filters, @Nullable String sortOrder) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.filters = filters; // Can be null
        this.sortOrder = sortOrder; // Can be null
    }

    public PageRequest(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.filters = null;
        this.sortOrder = null;
    }

    public static PageRequest defaultRequest() {
        return new PageRequest(0, 10, null, null); // Default page number, page size, filters, and sortOrder as null
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    @Nullable
    public String getFilters() {
        return filters;
    }

    @Nullable
    public String getSortOrder() {
        return sortOrder;
    }
}
