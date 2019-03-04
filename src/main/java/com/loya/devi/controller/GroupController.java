package com.loya.devi.controller;

import com.loya.devi.controller.component.DTOConverter;
import com.loya.devi.entity.Group;
import com.loya.devi.entity.dto.GroupDTO;
import com.loya.devi.service.interfaces.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Processes the request and returns the response as JSON.
 *
 * @author DEVIAPHAN
 */
@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    GroupService groupService;

    @Autowired
    DTOConverter dtoConverter;

    /**
     * method return all group entities
     *
     * @param locale of message
     * @return getAll group entities in the database.
     */
    @GetMapping
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<GroupDTO> getAll(Locale locale) {
        return groupService.getAll(locale).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * method return group
     *
     * @param id of group
     * @return group entity by ID in the database.
     */
    @GetMapping("/{id}")
    public GroupDTO getById(@PathVariable Long id, Locale locale) {
        return convertToDto(groupService.getById(id, locale));
    }

    /**
     * method return all groups by teacher id
     *
     * @param id     of teacher
     * @param locale of message
     * @return all groups by teacher id
     */
    @GetMapping("/getByTeacherId/{id}")
    public List<GroupDTO> getGroupsOfTeacherById(@PathVariable Long id, Locale locale) {
        return groupService.getGroupsOfTeacherById(id, locale).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * method save group
     *
     * @param groupDTO      entity
     * @param curatorId     of teacher
     * @param teacherIdList consist of teachers
     * @return added group entity in the database.
     */
    @PostMapping
    public GroupDTO save(@RequestBody GroupDTO groupDTO, @RequestParam Long curatorId, @RequestParam Long[] teacherIdList, Locale locale) {
        Group group = groupService.save(convertToEntity(groupDTO), curatorId, teacherIdList, locale);
        return convertToDto(group);
    }

    /**
     * method update group by id
     *
     * @param groupDTO      entity
     * @param groupId       of group
     * @param curatorId     of teacher
     * @param teacherIdList consist of teachers
     * @return updated group entity in the database.
     */
    @PutMapping("/{groupId}")
    public GroupDTO updateById(@RequestBody GroupDTO groupDTO, @PathVariable Long groupId, @RequestParam Long curatorId, @RequestParam Long[] teacherIdList, Locale locale) {
        Group group = groupService.updateById(convertToEntity(groupDTO), groupId, curatorId, teacherIdList, locale);
        return convertToDto(group);
    }

    /**
     * method delete group by id
     * id the group entity to be removed from the database
     *
     * @param locale of message
     */
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id, Locale locale) {
        groupService.deleteById(id, locale);
    }

    /**
     * method return all sorted groups using oracle package
     *
     * @return All entity in the database.
     */
    @GetMapping("/sort-asc")
    public List<GroupDTO> getSortedGroups() {
        return groupService.getSortedGroups().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * method return all sorted revert groups using oracle package
     *
     * @return All entity in the database.
     */
    @GetMapping("/sort-desc")
    public List<GroupDTO> getSortedRevertTeachers() {
        return groupService.getSortedRevertGroups().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private GroupDTO convertToDto(Group group) {
        return (GroupDTO) dtoConverter.convert(group, GroupDTO.class);
    }

    private Group convertToEntity(GroupDTO groupDTO) {
        return (Group) dtoConverter.convert(groupDTO, Group.class);
    }
}