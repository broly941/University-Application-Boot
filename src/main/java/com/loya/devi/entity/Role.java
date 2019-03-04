package com.loya.devi.entity;

import javax.persistence.*;

/**
 * The class that stores the state of the entity.
 *
 * @author ilya.korzhavin
 */
@Entity
@Table(name = "UserRoles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleId")
    private Long id;

    @Column(name = "RoleName")
    private String name;

    public Role() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
