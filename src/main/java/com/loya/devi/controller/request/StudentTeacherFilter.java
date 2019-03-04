package com.loya.devi.controller.request;

/**
 * Storing and management data for filtering
 */
public class StudentTeacherFilter extends Filter {
    private String firstName;
    private String lastName;

    public StudentTeacherFilter(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public Boolean isFilterExist() {
        return firstName != null && lastName != null;
    }
}
