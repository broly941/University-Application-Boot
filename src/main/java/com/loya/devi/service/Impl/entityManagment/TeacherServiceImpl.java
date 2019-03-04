package com.loya.devi.service.Impl.entityManagment;

import com.loya.devi.controller.request.PageRequestParameters;
import com.loya.devi.controller.request.StudentTeacherFilter;
import com.loya.devi.controller.response.ValidationStatus;
import com.loya.devi.entity.Teacher;
import com.loya.devi.exception.SQLQueryException;
import com.loya.devi.repository.TeacherRepository;
import com.loya.devi.service.Impl.fileReader.validate.TeacherValidatorImpl;
import com.loya.devi.service.interfaces.BaseService;
import com.loya.devi.service.interfaces.EntitiesValidationService;
import com.loya.devi.service.interfaces.QueryExecutor;
import com.loya.devi.service.interfaces.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Business Logic Service Class
 *
 * @author DEVIAPHAN
 */
@Service
public class TeacherServiceImpl implements TeacherService {
    private static final String TEACHER = "teacher";
    private static final String TEACHERS = "teachers";
    private static final String GET_TEACHER_BY_ID = "Get teacher by id";
    private static final String UPDATE_TEACHER_BY_ID = "Update teacher by id";
    private static final String DELETED_BY_ID = "deleteById";
    private static final String UPDATE_BY_ID = "updateById";
    private static final String ADD = "add";
    private static final String GET_BY_ID = "getById";
    private static final String GET_ALL = "getAll";
    private static final String GET_TEACHERS_OF_GROUP_BY_ID = "getTeachersOfGroupById";
    private static final String GET_TEACHERS_OF_GROUP_BY_ID1 = "Get teachers of group by id";

    private static final String GET_SORTED_TEACHER_ASC = "{ ? = call modify_data_pkg.get_sorted_teachers_asc() }";
    private static final String GET_SORTED_TEACHER_DESC = "{ ? = call modify_data_pkg.get_sorted_teachers_desc() }";

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private BaseService<Teacher> teacherBaseService;

    @Autowired
    private EntitiesValidationService entitiesValidationService;

    @Autowired
    private TeacherValidatorImpl teacherValidator;

    @Autowired
    private QueryExecutor queryExecutor;

    /**
     * method return all teachers
     *
     * @param locale of messages
     * @return getAll teachers entities in the database.
     */
    @Override
    public List<Teacher> getAll(Locale locale) {
        return teacherBaseService.getAll(teacherRepository::findAll, locale, GET_ALL, TEACHERS);
    }

    /**
     * method return teacher by id
     *
     * @param id     of teacher
     * @param locale of messages
     * @return teacher entity by ID in the database.
     */
    @Override
    public Teacher getById(Long id, Locale locale) {
        return teacherBaseService.get(id, teacherRepository::findById, locale, GET_BY_ID, TEACHER, GET_TEACHER_BY_ID);
    }

    /**
     * method return all teacher by group id
     *
     * @param id     of entity
     * @param locale of messages
     * @return List of teachers
     */
    @Override
    public List<Teacher> getTeachersOfGroupById(Long id, Locale locale) {
        return teacherBaseService.getAll(id, teacherRepository::findAllTeachersOfGroupById, locale, GET_TEACHERS_OF_GROUP_BY_ID, TEACHERS, GET_TEACHERS_OF_GROUP_BY_ID1);
    }

    /**
     * method return teacher by name
     *
     * @param firstName of entity
     * @param lastName  of entity
     * @return Optional teacher
     */
    @Override
    public Optional<Teacher> getTeacherByName(String firstName, String lastName) {
        return teacherRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    /**
     * method save teacher and return it
     *
     * @param teacher entity
     * @param locale  of messages
     * @return added teacher entity in the database.
     */
    @Override
    @Transactional
    public Teacher save(Teacher teacher, Locale locale) {
        return teacherBaseService.save(teacher, teacherRepository::save, locale, ADD, TEACHER);
    }

    /**
     * method update teacher by id and return it
     *
     * @param teacher entity
     * @param locale  of messages
     * @return updated teacher entity in the database.
     */
    @Override
    @Transactional
    public Teacher updateById(Teacher teacher, Long teacherId, Locale locale) {
        Teacher currentTeacher = teacherBaseService.get(teacherId, teacherRepository::findById, locale, UPDATE_BY_ID, TEACHER, UPDATE_TEACHER_BY_ID);
        currentTeacher.setFirstName(teacher.getFirstName());
        currentTeacher.setLastName(teacher.getLastName());
        return teacherBaseService.save(currentTeacher, teacherRepository::save, locale, UPDATE_BY_ID, TEACHER, teacherId);
    }

    /**
     * method delete teacher by id
     *
     * @param id     the teacher entity to be removed from the database
     * @param locale of messages
     */
    @Override
    public void deleteById(Long id, Locale locale) {
        teacherBaseService.deleteById(id, teacherRepository::deleteById, locale, DELETED_BY_ID, TEACHER);
    }

    /**
     * method validate parseEntities and returned validationStatus
     *
     * @param parsedEntities map of entities file
     * @param validEntities  map of validation entities
     * @param locale         of message
     * @return validationStatus
     */
    @Override
    public ValidationStatus validate(ConcurrentHashMap<Integer, List<Object>> parsedEntities, ConcurrentHashMap<Integer, Object> validEntities, Locale locale) {
        return entitiesValidationService.validateParsedEntities(parsedEntities, validEntities, locale, teacherValidator::validate);
    }

    /**
     * Method save entity from map of valid entities
     *
     * @param validEntities of entity in file.
     * @param locale        of messages.
     */
    @Override
    @Transactional
    public void save(Map<Integer, Object> validEntities, Locale locale) {
        Map<Integer, Teacher> teacherEntities = validEntities.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (Teacher) e.getValue()));
        teacherBaseService.saveAll(new ArrayList<>(teacherEntities.values()), teacherRepository::saveAll, locale, ADD, TEACHERS);
    }

    /**
     * method return all sorted teachers
     *
     * @return All entity in the database.
     */
    @Override
    public List<Teacher> getSortedTeachers() {
        return queryExecutor.execute(GET_SORTED_TEACHER_ASC, this::initTeachersFromResultList);
    }

    /**
     * method return all sorted revert teachers
     *
     * @return All entity in the database.
     */
    @Override
    public List<Teacher> getSortedRevertTeachers() {
        return queryExecutor.execute(GET_SORTED_TEACHER_DESC, this::initTeachersFromResultList);
    }

    /**
     * method set param to entity and return list of them
     *
     * @param resultSet which stored result from query
     * @return All entity in the database.
     */
    private List<Teacher> initTeachersFromResultList(ResultSet resultSet) {
        List<Teacher> teachers = new ArrayList<>();
        try {
            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                teachers.add(new Teacher(id, firstName, lastName));
            }
        } catch (SQLException ex) {
            throw new SQLQueryException();
        }
        return teachers;
    }

    /**
     * method takes parameters and result entity as result of filtering or list of entities as result of pagination
     *
     * @param parameters is object which stores the parameters for pagination
     * @param locale     of message
     * @return list of teachers
     */
    @Override
    public Page<Teacher> getByFilter(PageRequestParameters parameters, Locale locale) {
        StudentTeacherFilter filter = (StudentTeacherFilter) parameters.getFilter();
        Pageable pageable = parameters.getPage().getPageable();
        return teacherBaseService.getByFilter(filter, pageable,
                () -> teacherRepository.findAllByFirstNameAndLastName(filter.getFirstName(), filter.getLastName(), pageable),
                () -> teacherRepository.findAll(pageable));
    }
}
