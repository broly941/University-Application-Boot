package com.loya.devi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * The class that stores the state of the entity.
 *
 * @author DEVIAPHAN
 */
@Entity
@Table(name = "GroupOfUniversity")
public class Group implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GroupId")
    private Long id;

    @Column(name = "Number")
    private String number;

    @OneToOne
    @JoinColumn(name = "TeacherId")
    private Teacher teacher;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
    private List<Student> students;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "GroupTeacher", joinColumns = @JoinColumn(name = "GroupId"), inverseJoinColumns = @JoinColumn(name = "TeacherId"))
    private List<Teacher> teachers;

    public Group() {
    }

    public Group(long id, String number) {
        this.id = id;
        this.number = number;
    }

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

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }
}
