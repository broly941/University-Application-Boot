package com.loya.devi.entity;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * The class that stores the state of the entity.
 *
 * @author DEVIAPHAN
 */
@Entity
@Table(name = "Student")
public class Student {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "StudentId")
    private Long id;

    @Column(name = "FirstName")
    private String firstName;

    @Column(name = "LastName")
    private String lastName;

    @ManyToOne
    @JoinColumn(name = "GroupId", referencedColumnName = "GroupId")
    private Group group;

    public Student() {
    }

    public Student(String firstName, String lastName, Group group) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.group = group;
    }

    public Student(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Student(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

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

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
