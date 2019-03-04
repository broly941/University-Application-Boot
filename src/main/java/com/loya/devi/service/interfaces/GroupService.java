package com.loya.devi.service.interfaces;

import com.loya.devi.controller.request.PageRequestParameters;
import com.loya.devi.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * @author DEVIAPHAN on 21.12.2018
 * @project university
 */
public interface GroupService {
    List<Group> getAll(Locale locale);

    Group getById(Long id, Locale locale);

    @Transactional
    Optional<Group> getByNumber(String groupName);

    @Transactional
    Group save(Group group, Long curatorId, Long[] teacherIdList, Locale locale);

    @Transactional
    Group updateById(Group group, Long groupId, Long curatorId, Long[] teacherIdList, Locale locale);

    void deleteById(Long id, Locale locale);

    List<Group> getGroupsOfTeacherById(Long id, Locale locale);

    List<Group> getSortedGroups();

    List<Group> getSortedRevertGroups();

    Page<Group> getByFilter(PageRequestParameters parameters, Locale locale);
}
