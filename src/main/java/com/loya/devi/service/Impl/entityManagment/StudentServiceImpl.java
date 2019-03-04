package com.loya.devi.service.Impl.entityManagment;

import com.loya.devi.controller.request.PageRequestParameters;
import com.loya.devi.controller.request.StudentTeacherFilter;
import com.loya.devi.controller.response.ValidationStatus;
import com.loya.devi.entity.Student;
import com.loya.devi.exception.SQLQueryException;
import com.loya.devi.repository.StudentRepository;
import com.loya.devi.service.interfaces.*;
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
public class StudentServiceImpl implements StudentService {
    private static final String STUDENT = "student";
    private static final String STUDENTS = "students";
    private static final String UPDATE_STUDENT_BY_ID = "Update student by id";
    private static final String UPDATE_BY_ID = "updateById";
    private static final String DELETED_BY_ID = "deleteById";
    private static final String ADD = "add";
    private static final String GET_STUDENT_BY_ID = "Get student by id";
    private static final String GET_BY_ID = "getById";
    private static final String GET_ALL = "getAll";
    private static final String GET_STUDENTS_OF_GROUP_BY_ID = "getStudentsOfGroupById";
    private static final String GET_STUDENTS_BY_GROUP_ID = "Get students by group id";

    private static final String GET_SORTED_STUDENT_ASC = "{ ? = call modify_data_pkg.get_sorted_students_asc() }";
    private static final String GET_SORTED_STUDENT_DESC = "{ ? = call modify_data_pkg.get_sorted_students_desc() }";

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GroupService groupService;

    @Autowired
    private BaseService<Student> studentBaseService;

    @Autowired
    private EntitiesValidationService entitiesValidationService;

    @Autowired
    private StudentValidator studentValidator;

    @Autowired
    private QueryExecutor queryExecutor;

    /**
     * method return all students
     *
     * @param locale of messages
     * @return getAll student entities in the database.
     */
    @Override
    public List<Student> getAll(Locale locale) {
        return studentBaseService.getAll(studentRepository::findAll, locale, GET_ALL, STUDENTS);
    }

    /**
     * method return student by id
     *
     * @param id     of student
     * @param locale of messages
     * @return student entity by ID in the database.
     */
    @Override
    public Student getById(Long id, Locale locale) {
        return studentBaseService.get(id, studentRepository::findById, locale, GET_BY_ID, STUDENT, GET_STUDENT_BY_ID);
    }

    /**
     * method return all students by group id
     *
     * @param id     of entity
     * @param locale of messages
     * @return List of student
     */
    @Override
    public List<Student> getStudentsOfGroupById(Long id, Locale locale) {
        return studentBaseService.getAll(id, studentRepository::findAllByGroup_Id, locale, GET_STUDENTS_OF_GROUP_BY_ID, STUDENTS, GET_STUDENTS_BY_GROUP_ID);
    }

    /**
     * method return student by name
     *
     * @param firstName of entity
     * @param lastName  of entity
     * @return Optional student
     */
    @Override
    public Optional<Student> getByName(String firstName, String lastName) {
        return studentRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    /**
     * method save student and return it
     *
     * @param student entity
     * @param groupId of group
     * @param locale  of messages
     * @return added student entity in the database.
     */
    @Override
    @Transactional
    public Student save(Student student, Long groupId, Locale locale) {
        student.setGroup(groupService.getById(groupId, locale));
        return studentBaseService.save(student, studentRepository::save, locale, ADD, STUDENT);
    }

    /**
     * method save student and return it
     *
     * @param student entity
     * @param locale  of messages
     * @return added student entity in the database.
     */
    @Override
    @Transactional
    public Student save(Student student, Locale locale) {
        return studentBaseService.save(student, studentRepository::save, locale, ADD, STUDENT);
    }

    /**
     * method update student by id and return it
     *
     * @param student   entity
     * @param studentId of student
     * @param groupId   of group
     * @param locale    of messages
     * @return updated student entity in the database.
     */
    @Override
    @Transactional
    public Student updateById(Student student, Long studentId, Long groupId, Locale locale) {
        Student currentStudent = studentBaseService.get(studentId, studentRepository::findById, locale, UPDATE_BY_ID, STUDENT, UPDATE_STUDENT_BY_ID);
        currentStudent.setFirstName(student.getFirstName());
        currentStudent.setLastName(student.getLastName());
        currentStudent.setGroup(groupService.getById(groupId, locale));
        return studentBaseService.save(currentStudent, studentRepository::save, locale, UPDATE_BY_ID, STUDENT, studentId);
    }

    /**
     * method delete student by id
     *
     * @param locale of messages
     * @param id     the student entity to be removed from the database
     */
    @Override
    public void deleteById(Long id, Locale locale) {
        studentBaseService.deleteById(id, studentRepository::deleteById, locale, DELETED_BY_ID, STUDENT);
    }

    /**
     * method return student by name and group name
     *
     * @param firstName of entity
     * @param lastName  of entity
     * @param groupName of entity group
     * @return return student
     */
    @Override
    public Optional<Student> getStudentByNameAndGroupName(String firstName, String lastName, String groupName) {
        return studentRepository.findByFirstNameAndLastNameAndGroup_Number(firstName, lastName, groupName);
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
        return entitiesValidationService.validateParsedEntities(parsedEntities, validEntities, locale, studentValidator::validate);
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
        Map<Integer, Student> studentEntities = validEntities.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (Student) e.getValue()));
        studentBaseService.saveAll(new ArrayList<>(studentEntities.values()), studentRepository::saveAll, locale, ADD, STUDENTS);
    }


    /**
     * method return all sorted students
     *
     * @return All entity in the database.
     */
    @Override
    public List<Student> getSortedStudents() {
        return queryExecutor.execute(GET_SORTED_STUDENT_ASC, this::initStudentsFromResultList);
    }

    /**
     * method return all sorted revert students
     *
     * @return All entity in the database.
     */
    @Override
    public List<Student> getSortedRevertStudents() {
        return queryExecutor.execute(GET_SORTED_STUDENT_DESC, this::initStudentsFromResultList);
    }

    /**
     * method set param to entity and return list of them
     *
     * @param resultSet which stored result from query
     * @return All entity in the database.
     */
    private List<Student> initStudentsFromResultList(ResultSet resultSet) {
        List<Student> teachers = new ArrayList<>();
        try {
            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                teachers.add(new Student(id, firstName, lastName));
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
     * @return list of students
     */
    @Override
    public Page<Student> getByFilter(PageRequestParameters parameters, Locale locale) {
        StudentTeacherFilter filter = (StudentTeacherFilter) parameters.getFilter();
        Pageable pageable = parameters.getPage().getPageable();
        return studentBaseService.getByFilter(filter, pageable,
                () -> studentRepository.findAllByFirstNameAndLastName(filter.getFirstName(), filter.getLastName(), pageable),
                () -> studentRepository.findAll(pageable));
    }
}