package com.loya.devi.repository;

import com.loya.devi.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * The class works with searching, retrieving and storing data from a database.
 *
 * @author ilya.korzhavin
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
