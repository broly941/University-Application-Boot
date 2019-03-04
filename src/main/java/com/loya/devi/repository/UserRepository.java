package com.loya.devi.repository;

import com.loya.devi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * The class works with searching, retrieving and storing data from a database.
 *
 * @author ilya.korzhavin
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

}
