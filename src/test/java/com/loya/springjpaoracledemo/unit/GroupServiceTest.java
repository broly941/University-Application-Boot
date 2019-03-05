package com.loya.springjpaoracledemo.unit;

import com.loya.devi.entity.Group;
import com.loya.devi.repository.GroupRepository;
import com.loya.devi.service.Impl.entityManagment.GroupServiceImpl;
import com.loya.devi.service.Impl.entityManagment.TeacherServiceImpl;
import com.loya.devi.service.interfaces.BaseService;
import org.junit.Assert;
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
public class GroupServiceTest {
    @InjectMocks
    GroupServiceImpl groupService;

    @Mock
    GroupRepository groupRepository;

    @Mock
    BaseService<Group> groupBaseService;

    @Mock
    TeacherServiceImpl teacherService;

    @Mock
    MessageSource messageSource;

    /**
     * Will return the list if all parameters are correct
     */
    @Test
    public void getAll() {
        List<Group> groupList = initializeGroupList();
        when(groupBaseService.getAll(any(Supplier.class), eq(Locale.ENGLISH), eq("getAll"), eq("groups")))
                .thenReturn(groupList);
        assertSame(groupList, groupService.getAll(Locale.ENGLISH));
    }

    /**
     * Will return an empty list if all parameters are correct
     */
    @Test
    public void getAll_NotFoundGroups() {
        when(groupBaseService.getAll(any(Supplier.class), eq(Locale.ENGLISH), eq("getAll"), eq("groups")))
                .thenReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), groupService.getAll(Locale.ENGLISH));
    }

    /**
     * Will return a record by ID if all parameters are correct
     */
    @Test
    public void getById() {
        Group group = initializeGroup((long) 1);
        when(groupBaseService.get(eq((long) 1), any(Function.class), eq(Locale.ENGLISH), eq("getById"), eq("group"), eq("Get group by id")))
                .thenReturn(group);
        assertSame(group, groupService.getById((long) 1, Locale.ENGLISH));
    }

    /**
     * Will thrown out an exception if record by ID cannot found
     */
    @Test(expected = EntityNotFoundException.class)
    public void getById_NotFoundGroup() {
        when(groupBaseService.get(eq((long) 2), any(Function.class), eq(Locale.ENGLISH), eq("getById"), eq("group"), eq("Get group by id")))
                .thenThrow(EntityNotFoundException.class);
        groupService.getById((long) 2, Locale.ENGLISH);
    }

    /**
     * Will return a record by name
     */
    @Test
    public void getByNumber() {
        Optional<Group> groupOptional = Optional.of(initializeGroup((long) 1));
        when(groupRepository.findByNumber("POIT-1"))
                .thenReturn(groupOptional);
        assertSame(groupOptional, groupService.getByNumber("POIT-1"));
    }


    /**
     * Will return a records by teacher id
     */
    @Test
    public void getGroupsOfTeacherById() {
        List<Group> groupList = initializeGroupList();
        when(groupBaseService.getAll(eq((long) 1), any(Function.class), eq(Locale.ENGLISH), eq("getGroupsByTeacherId"), eq("groups"), eq("Get groups by teacher id")))
                .thenReturn(groupList);
        assertSame(groupList, groupService.getGroupsOfTeacherById((long) 1, Locale.ENGLISH));
    }

    /**
     * Will save a record if all parameters are correct
     */
    @Test
    public void save() {
        Group group = initializeGroup((long) 1);
        when(groupBaseService.save(eq(group), any(UnaryOperator.class), eq(Locale.ENGLISH), eq("add"), eq("group")))
                .thenReturn(group);
        assertSame(group, groupService.save(group, (long) 2, new Long[]{}, Locale.ENGLISH));
    }

    /**
     * Will return a record if all parameters are correct
     */
    @Test
    public void updateById() {
        Group group = initializeGroup((long) 1);
        when(groupBaseService.get(eq((long) 1), any(Function.class), eq(Locale.ENGLISH), eq("updateById"), eq("group"), eq("Update group by id")))
                .thenReturn(group);
        when(groupBaseService.save(eq(group), any(UnaryOperator.class), eq(Locale.ENGLISH), eq("updateById"), eq("group"), eq((long) 1)))
                .thenReturn(group);
        assertSame(group, groupService.updateById(group, (long) 1, null, new Long[]{}, Locale.ENGLISH));
    }

    /**
     * Will thrown out an exception if entity cannot found
     */
    @Test(expected = EntityNotFoundException.class)
    public void updateById_NotFoundId() {
        when(groupBaseService.get(eq((long) 1), any(Function.class), eq(Locale.ENGLISH), eq("updateById"), eq("group"), eq("Update group by id")))
                .thenThrow(EntityNotFoundException.class);
        groupService.updateById(initializeGroup((long) 1), (long) 1, (long) 1, new Long[]{}, Locale.ENGLISH);
    }

    private List<Group> initializeGroupList() {
        List<Group> groupList = new ArrayList<>();
        groupList.add(initializeGroup((long) 1));
        groupList.add(initializeGroup((long) 2));
        return groupList;
    }

    private Group initializeGroup(long id) {
        Group group = new Group();
        group.setNumber("POIT-" + id);
        group.setId(id);

        return group;
    }
}