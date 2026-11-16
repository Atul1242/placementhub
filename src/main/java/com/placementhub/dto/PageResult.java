package com.placementhub.dto;

import java.util.List;

/**
 * Lightweight page wrapper for paginated DAO results.
 */
public class PageResult<T> {

    private final List<T> items;
    private final long totalItems;
    private final int page;
    private final int pageSize;

    public PageResult(List<T> items, long totalItems, int page, int pageSize) {
        this.items = items;
        this.totalItems = totalItems;
        this.page = page;
        this.pageSize = pageSize;
    }

    public List<T> getItems() {
        return items;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPages() {
        if (pageSize <= 0) {
            return 1;
        }
        return (int) Math.max(1, Math.ceil((double) totalItems / pageSize));
    }

    public boolean hasPrevious() {
        return page > 1;
    }

    public boolean hasNext() {
        return page < getTotalPages();
    }
}
