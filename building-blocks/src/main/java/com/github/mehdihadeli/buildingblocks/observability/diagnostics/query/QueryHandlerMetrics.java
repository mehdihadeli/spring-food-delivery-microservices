package com.github.mehdihadeli.buildingblocks.observability.diagnostics.query;

public interface QueryHandlerMetrics {
    <TQuery> void startExecuting(Class<TQuery> queryClass);

    <TQuery> void finishExecuting(Class<TQuery> queryClass);

    <TQuery> void failedQuery(Class<TQuery> queryClass);
}
