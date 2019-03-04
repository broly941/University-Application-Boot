package com.loya.devi.controller.request;

/**
 * Storing and management data for filtering
 */
public class Filter {
    private Integer id;

    public Filter() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean isFilterExist() {
        return id != null;
    }
}
