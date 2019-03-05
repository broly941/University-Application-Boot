package com.loya.springjpaoracledemo.unit;

import com.loya.devi.entity.Teacher;
import com.loya.devi.repository.TeacherRepository;
import com.loya.devi.service.Impl.entityManagment.TeacherServiceImpl;
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
public class TeacherServiceTest {

    @InjectMocks
    TeacherServiceImpl teacherService;

    @Mock
    TeacherRepository teacherRepository;

    @Mock
    BaseService<Teacher> teacherBaseService;

    @Mock
    EntitiesValidationService entitiesValidationService;

    @Mock
    MessageSource messageSource;

    /**
     * Will return the list if all parameters are correct
     */
    @Test
    public void getAll() {
        List<Teacher> teacherList = initializeTeacherList();
        when(teacherBaseService.getAll(any(Supplier.class), eq(Locale.ENGLISH), eq("getAll"), eq("teachers")))
                .thenReturn(teacherList);
        assertSame(teacherList, teacherService.getAll(Locale.ENGLISH));
    }

    /**
     * Will return an empty list if all parameters are correct
     */
    @Test
    public void getAll_NotFoundTeachers() {
        when(teacherBaseService.getAll(any(Supplier.class), eq(Locale.ENGLISH), eq("getAll"), eq("teachers")))
                .thenReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), teacherService.getAll(Locale.ENGLISH));
    }

    /**
     * Will return a record by ID if all parameters are correct
     */
    @Test
    public void getById() {
        Teacher teacher = initializeTeacher((long) 1);
        when(teacherBaseService.get(eq((long) 1), any(Function.class), eq(Locale.ENGLISH), eq("getById"), eq("teacher"), eq("Get teacher by id")))
                .thenReturn(teacher);
        assertSame(teacher, teacherService.getById((long) 1, Locale.ENGLISH));
    }

    /**
     * Will thrown out an exception if record by ID cannot found
     */
    @Test(expected = EntityNotFoundException.class)
    public void getById_NotFoundTeacher() {
        when(teacherBaseService.get(eq((long) 2), any(Function.class), eq(Locale.ENGLISH), eq("getById"), eq("teacher"), eq("Get teacher by id")))
                .thenThrow(EntityNotFoundException.class);
        teacherService.getById((long) 2, Locale.ENGLISH);
    }

    /**
     * Will return the list if all parameters are correct
     */
    @Test
    public void getTeachersOfGroupById() {
        List<Teacher> teacherList = initializeTeacherList();
        when(teacherBaseService.getAll(eq((long) 1), any(Function.class), eq(Locale.ENGLISH), eq("getTeachersOfGroupById"), eq("teachers"), eq("Get teachers of group by id")))
                .thenReturn(teacherList);
        assertSame(teacherList, teacherService.getTeachersOfGroupById((long) 1, Locale.ENGLISH));
    }

    /**
     * Will return a record by name
     */
    @Test
    public void getTeacherByName() {
        Optional<Teacher> teacherOptional = Optional.of(initializeTeacher((long) 1));
        when(teacherRepository.findByFirstNameAndLastName("First", "Last"))
                .thenReturn(teacherOptional);
        assertSame(teacherOptional, teacherService.getTeacherByName("First", "Last"));
    }

    /**
     * Will save a record if all parameters are correct
     */
    @Test
    public void save() {
        Teacher teacher = initializeTeacher((long) 1);
        when(teacherBaseService.save(eq(teacher), any(UnaryOperator.class), eq(Locale.ENGLISH), eq("add"), eq("teacher")))
                .thenReturn(teacher);
        assertSame(teacher, teacherService.save(teacher, Locale.ENGLISH));
    }

    /**
     * Will return a record if all parameters are correct
     */
    @Test
    public void updateById() {
        Teacher teacher = initializeTeacher((long) 1);
        when(teacherBaseService.get(eq((long) 1), any(Function.class), eq(Locale.ENGLISH), eq("updateById"), eq("teacher"), eq("Update teacher by id")))
                .thenReturn(teacher);
        when(teacherBaseService.save(eq(teacher), any(UnaryOperator.class), eq(Locale.ENGLISH), eq("updateById"), eq("teacher"), eq((long) 1)))
                .thenReturn(teacher);
        assertSame(teacher, teacherService.updateById(teacher, (long) 1, Locale.ENGLISH));
    }

    /**
     * Will thrown out an exception if entity cannot found
     */
    @Test(expected = EntityNotFoundException.class)
    public void updateById_NotFoundId() {
        when(teacherBaseService.get(eq((long) 1), any(Function.class), eq(Locale.ENGLISH), eq("updateById"), eq("teacher"), eq("Update teacher by id")))
                .thenThrow(EntityNotFoundException.class);
        teacherService.updateById(initializeTeacher((long) 1), (long) 1, Locale.ENGLISH);
    }

    private List<Teacher> initializeTeacherList() {
        List<Teacher> teacherList = new ArrayList<>();
        teacherList.add(initializeTeacher((long) 1));
        teacherList.add(initializeTeacher((long) 2));
        return teacherList;
    }

    private Teacher initializeTeacher(long id) {
        Teacher teacher = new Teacher();
        teacher.setFirstName("First");
        teacher.setLastName("Last");
        teacher.setId(id);

        return teacher;
    }
}