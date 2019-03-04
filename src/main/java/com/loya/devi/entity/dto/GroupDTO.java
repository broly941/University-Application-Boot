package com.loya.devi.entity.dto;

/**
 * Data Transfer Object of Group
 *
 * @author DEVIAPHAN on 10.01.2019
 * @project university
 */
public class GroupDTO {
    private Long id;

    private String number;

    private TeacherDTO teacher;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public TeacherDTO getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherDTO convertToDto) {
        this.teacher = convertToDto;
    }
}
