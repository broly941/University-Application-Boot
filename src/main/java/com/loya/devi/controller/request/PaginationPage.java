package com.loya.devi.controller.request;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Storing and management a page for pagination
 */
public class PaginationPage {
    private Pageable pageable = null;

    public PaginationPage(Integer page, Integer number) {
        if (page != null && number != null) {
            page--;
            if (page < 0) {
                throw new RuntimeException("Page is no valid");
            } else {
                pageable = PageRequest.of(page, number);
            }
        }
    }

    public Pageable getPageable() {
        return pageable;
    }

    public Boolean isPageExist() {
        return pageable != null;
    }
}
