package com.loya.devi.entity.dto;

/**
 * Data Transfer Object of Student
 *
 * @author DEVIAPHAN on 02.01.2019
 * @project university
 */
public class StudentDTO {
    private Long id;

    private String firstName;

    private String lastName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
