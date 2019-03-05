package com.loya.springjpaoracledemo.unit;

import com.loya.devi.entity.Student;
import com.loya.devi.repository.StudentRepository;
import com.loya.devi.service.Impl.entityManagment.GroupServiceImpl;
import com.loya.devi.service.Impl.entityManagment.StudentServiceImpl;
import com.loya.devi.service.interfaces.BaseService;
import com.loya.devi.service.interfaces.EntitiesValidationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Test for Business Logic Service Class
 *
 * @author DEVIAPHAN
 */
@RunWith(MockitoJUnitRunner.class)
public class StudentServiceTest {

    @InjectMocks
    StudentServiceImpl studentService;

    @Mock
    GroupServiceImpl groupService;

    @Mock
    StudentRepository studentRepository;

    @Mock
    BaseService<Student> studentBaseService;

    @Mock
    EntitiesValidationService entitiesValidationService;

    @Mock
    MessageSource messageSource;

    /**
     * Will return the list if all parameters are correct
     */
    @Test
    public void getAll() {
        List<Student> studentList = initializeStudentList();
        when(studentBaseService.getAll(any(Supplier.class), eq(Locale.ENGLISH), eq("getAll"), eq("students")))
                .thenReturn(studentList);
        assertSame(studentList, studentService.getAll(Locale.ENGLISH));
    }

    /**
     * Will return an empty list if all parameters are correct
     */
    @Test
    public void getAll_NotFoundStudents() {
        when(studentBaseService.getAll(any(Supplier.class), eq(Locale.ENGLISH), eq("getAll"), eq("students")))
                .thenReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), studentService.getAll(Locale.ENGLISH));
    }

    /**
     * Will return a record by ID if all parameters are correct
     */
    @Test
    public void getById() {
        Student student = initializeStudent((long) 1);
        when(studentBaseService.get(eq((long) 1), any(Function.class), eq(Locale.ENGLISH), eq("getById"), eq("student"), eq("Get student by id")))
                .thenReturn(student);
        assertSame(student, studentService.getById((long) 1, Locale.ENGLISH));
    }

    /**
     * Will thrown out an exception if record by ID cannot found
     */
    @Test(expected = EntityNotFoundException.class)
    public void getById_NotFoundStudent() {
        when(studentBaseService.get(eq((long) 2), any(Function.class), eq(Locale.ENGLISH), eq("getById"), eq("student"), eq("Get student by id")))
                .thenThrow(EntityNotFoundException.class);
        studentService.getById((long) 2, Locale.ENGLISH);
    }

    /**
     * Will return a list of records by group id
     */
    @Test
    public void getStudentsOfGroupById() {
        List<Student> studentList = initializeStudentList();
        when(studentBaseService.getAll(eq((long) 1), any(Function.class), eq(Locale.ENGLISH), eq("getStudentsOfGroupById"), eq("students"), eq("Get students by group id")))
                .thenReturn(studentList);
        assertSame(studentList, studentService.getStudentsOfGroupById((long) 1, Locale.ENGLISH));
    }

    /**
     * Will return a record by name
     */
    @Test
    public void getByName() {
        Optional<Student> studentOptional = Optional.of(initializeStudent((long) 1));
        when(studentRepository.findByFirstNameAndLastName("First", "Last"))
                .thenReturn(studentOptional);
        assertSame(studentOptional, studentService.getByName("First", "Last"));
    }

    /**
     * Will save a record if all parameters are correct
     */
    @Test
    public void save() {
        Student student = initializeStudent((long) 1);
        when(studentBaseService.save(eq(student), any(UnaryOperator.class), eq(Locale.ENGLISH), eq("add"), eq("student")))
                .thenReturn(student);
        assertSame(student, studentService.save(student, (long) 4, Locale.ENGLISH));
    }


    /**
     * Will return a record if all parameters are correct
     */
    @Test
    public void updateById() {
        Student student = initializeStudent((long) 1);
        when(studentBaseService.get(eq((long) 1), any(Function.class), eq(Locale.ENGLISH), eq("updateById"), eq("student"), eq("Update student by id")))
                .thenReturn(student);
        when(studentBaseService.save(eq(student), any(UnaryOperator.class), eq(Locale.ENGLISH), eq("updateById"), eq("student"), eq((long) 1)))
                .thenReturn(student);
        assertSame(student, studentService.updateById(student, (long) 1, (long) 1, Locale.ENGLISH));
    }

    /**
     * Will thrown out an exception if ID will null
     */
    @Test(expected = EntityNotFoundException.class)
    public void updateById_NotFoundId() {
        when(studentBaseService.get(eq((long) 1), any(Function.class), eq(Locale.ENGLISH), eq("updateById"), eq("student"), eq("Update student by id")))
                .thenThrow(EntityNotFoundException.class);
        studentService.updateById(initializeStudent((long) 1), (long) 1, (long) 1, Locale.ENGLISH);
    }

    /**
     * Will return boolean value depending isFilterExist entity or not
     */
    @Test
    public void getStudentByNameAndGroupName() {
        Optional<Student> optionalStudent = Optional.of(initializeStudent((long) 1));
        when(studentRepository.findByFirstNameAndLastNameAndGroup_Number("first", "last", "group"))
                .thenReturn(optionalStudent);
        assertSame(optionalStudent, studentService.getStudentByNameAndGroupName("first", "last", "group"));
    }

    private List<Student> initializeStudentList() {
        List<Student> studentList = new ArrayList<>();
        studentList.add(initializeStudent((long) 1));
        studentList.add(initializeStudent((long) 2));
        return studentList;
    }

    private Student initializeStudent(long id) {
        Student student = new Student();
        student.setFirstName("First");
        student.setLastName("Last");
        student.setId(id);

        return student;
    }
}