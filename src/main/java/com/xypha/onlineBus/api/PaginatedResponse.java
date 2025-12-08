package com.xypha.onlineBus.api;

import com.xypha.onlineBus.routes.Dto.RouteResponse;
import com.xypha.onlineBus.routes.Entity.Route;

import java.util.List;

public class PaginatedResponse <T> {
    private int offset;
    private int limit;
    private long totalItems;
    private List<T> contents;

    public PaginatedResponse(int offset, int limit, long totalItems, List<T> contents) {
        this.offset = offset;
        this.limit = limit;
        this.totalItems = totalItems;
        this.contents = contents;
    }





    public PaginatedResponse(int offset, int size, List<RouteResponse> routes) {
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public List<T> getContents() {
        return contents;
    }

    public void setContents(List<T> contents) {
        this.contents = contents;
    }
}
