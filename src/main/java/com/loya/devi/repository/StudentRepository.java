package com.loya.devi.repository;

import com.loya.devi.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * The class works with searching, retrieving and storing data from a database.
 *
 * @author DEVIAPHAN
 */
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByFirstNameAndLastNameAndGroup_Number(String firstName, String lastName, String number);

    Optional<Student> findByFirstNameAndLastName(String firstName, String lastName);

    List<Student> findAllByGroup_Id(Long id);

    Page<Student> findAll(Pageable pageable);

    Page<Student> findAllByFirstNameAndLastName(String firstName, String lastName, Pageable pageable);
}
