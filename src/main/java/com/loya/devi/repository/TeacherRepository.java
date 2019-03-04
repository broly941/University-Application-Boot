package com.loya.devi.repository;

import com.loya.devi.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * The class works with searching, retrieving and storing data from a database.
 *
 * @author DEVIAPHAN
 */
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    @Query(value = "SELECT * FROM teacher WHERE Exists (select 1 from groupteacher where groupteacher.GroupId = ?1 and teacher.TeacherId = groupteacher.TeacherId)", nativeQuery = true)
    List<Teacher> findAllTeachersOfGroupById(Long id);

    Optional<Teacher> findByFirstNameAndLastName(String firstName, String lastName);

    Page<Teacher> findAll(Pageable pageable);

    Page<Teacher> findAllByFirstNameAndLastName(String firstName, String lastName, Pageable pageable);
}
