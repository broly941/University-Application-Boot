package com.loya.devi.controller.request;

/**
 * Storing and management data for filtering
 */
public class GroupFilter extends Filter {
    private String groupNumber;

    public GroupFilter(String groupNumber ) {
        this.groupNumber = groupNumber;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    @Override
    public Boolean isFilterExist() {
        return groupNumber != null;
    }
}
