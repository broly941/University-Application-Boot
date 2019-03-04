package com.loya.devi.controller.request;

import javax.persistence.EntityNotFoundException;

/**
 * Storing and management data for pagination
 *
 * @param <T> for parametrize filter
 */
public class PageRequestParameters<T extends Filter> {
    private PaginationPage page = null;
    private T filter = null;

    public PageRequestParameters(PaginationPage page, T filter) {
        if (!page.isPageExist() && !filter.isFilterExist()) {
            throw new EntityNotFoundException("Missing all needed parameters");
        }
        this.page = page;
        this.filter = filter;
    }

    public PaginationPage getPage() {
        return page;
    }

    public T getFilter() {
        return filter;
    }
}
