package com.github.mehdihadeli.buildingblocks.abstractions.core.paging;

import java.util.List;

public interface IPageList<T> {
    int currentStartIndex();

    int currentEndIndex();

    int totalPages();

    boolean hasPrevious();

    boolean hasNext();

    List<T> items();

    int totalCount();

    int pageNumber();

    int pageSize();

    <TR> IPageList<TR> mapTo(java.util.function.Function<T, TR> map);
}
