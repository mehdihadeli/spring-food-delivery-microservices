package com.github.mehdihadeli.buildingblocks.core.pagination;

import com.github.mehdihadeli.buildingblocks.abstractions.core.paging.IPageList;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;

public record PageList<T>(List<T> items, int pageNumber, int pageSize, int totalCount) implements IPageList<T> {

    @Override
    public int currentStartIndex() {
        return totalCount == 0 ? 0 : ((pageNumber - 1) * pageSize) + 1;
    }

    @Override
    public int currentEndIndex() {
        return totalCount == 0 ? 0 : currentStartIndex() + pageSize - 1;
    }

    @Override
    public int totalPages() {
        return (int) Math.ceil(totalCount / (double) pageSize);
    }

    @Override
    public boolean hasPrevious() {
        return pageNumber > 1;
    }

    @Override
    public boolean hasNext() {
        return pageNumber < totalPages();
    }

    // Static empty instance of PageList
    public static <T> PageList<T> empty() {
        return new PageList<>(List.of(), 0, 0, 0);
    }

    // Static factory method to create a PageList instance
    public static <T> PageList<T> create(List<T> items, int pageNumber, int pageSize, int totalItems) {
        return new PageList<>(items, pageNumber, pageSize, totalItems);
    }

    // Method that accepts only the Page<T> and performs no mapping.
    public static <T> PageList<T> fromSpringPage(Page<T> page) {
        return PageList.create(page.getContent(), page.getNumber() + 1, page.getSize(), (int) page.getTotalElements());
    }

    // Method that accepts a Function<T, TR> to map the items, or null for no mapping.
    public static <T, TR> PageList<TR> fromSpringPage(Page<T> page, Function<T, TR> mapTo) {
        // If mapTo is provided, apply the function to the items, otherwise, use the original items
        List<TR> mappedItems = (mapTo != null)
                ? page.getContent().stream().map(mapTo).toList() // If mapTo is not null, map items
                : (List<TR>) page.getContent(); // If mapTo is null, use the original items

        return PageList.create(mappedItems, page.getNumber(), page.getSize(), (int) page.getTotalElements());
    }

    // Mapping function to create a new PageList with mapped items
    @Override
    public <TR> IPageList<TR> mapTo(Function<T, TR> map) {
        List<TR> mappedItems = items.stream().map(map).toList();
        return PageList.create(mappedItems, pageNumber, pageSize, totalCount);
    }
}
