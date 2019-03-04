package com.loya.devi.service.Impl.fileReader.validate;

import com.loya.devi.entity.Group;
import com.loya.devi.entity.Student;
import com.loya.devi.service.interfaces.EntitiesValidationService;
import com.loya.devi.service.interfaces.GroupService;
import com.loya.devi.service.interfaces.StudentService;
import com.loya.devi.service.interfaces.StudentValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Predicate;

/**
 * The class performs task validation.
 *
 * @author DEVIAPHAN on 1/28/2019
 * @project university
 */
@Component
public class StudentValidatorImpl implements StudentValidator {
    private static final String DUPLICATE = "DUPLICATE";
    @Autowired
    private StudentService studentService;

    @Autowired
    private GroupService groupService;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    EntitiesValidationService entitiesValidationService;

    @Autowired
    private MessageSource messageSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);

    private static final String STUDENT_ALREADY_EXISTS = "STUDENT_ALREADY_EXISTS";
    private static final String GROUP_DOES_NOT_EXIST = "GROUP_DOES_NOT_EXIST";

    private static final Predicate<List<Object>> allowableColumnPredicate = value -> value.size() != 3;
    private static final Predicate<List<Object>> instanceStringPredicate = value -> value.stream().allMatch(v -> v instanceof String);

    /**
     * the method starts validation and returns the stub - Void
     *
     * @param parameters is class that contains parameters
     * @return the stub - null
     */
    @Override
    public Void validate(ValidationParameters parameters) {
        long startTime = System.currentTimeMillis();
        Group group = null;
        String firstName = null;
        String lastName = null;
        String groupName;
        List<String> rowErrors = new ArrayList<>();
        List<Object> values = parameters.getValue();
        Locale locale = parameters.getLocale();
        if (entitiesValidationService.isValueStringAndHasReqColumn(instanceStringPredicate, allowableColumnPredicate, rowErrors, values, locale)) {
            firstName = values.get(0).toString();
            lastName = values.get(1).toString();
            groupName = values.get(2).toString();
            validateStudent(locale, firstName, lastName, rowErrors);
            group = validateGroup(locale, groupName, rowErrors);
            validateDuplicate(locale, parameters.getDuplicateSet(), firstName, lastName, rowErrors);
        }
        entitiesValidationService.fillValidationStatus(parameters.getValidationStatus(), parameters.getKey(), new Student(firstName, lastName, group), locale, rowErrors, parameters.getValidEntities());
        getRuntimeStatistic(startTime);
        return null;
    }

    private void getRuntimeStatistic(long startTime) {
        long time = System.currentTimeMillis() - startTime;
        LOGGER.debug(Thread.currentThread().getName() + ": " + time + " millis");
    }

    private void validateDuplicate(Locale locale, CopyOnWriteArraySet<String> duplicateSet, String firstName, String lastName, List<String> rowErrors) {
        if (!duplicateSet.add(firstName + " " + lastName)) {
            rowErrors.add(messageSource.getMessage(DUPLICATE, new Object[]{}, locale));
        }
    }

    private void validateStudent(Locale locale, String firstName, String lastName, List<String> rowErrors) {
        Optional<Student> optionalStudent = studentService.getByName(firstName, lastName);
        if (optionalStudent.isPresent()) {
            rowErrors.add(messageSource.getMessage(StudentValidatorImpl.STUDENT_ALREADY_EXISTS, new Object[]{}, locale));
        }
    }

    private Group validateGroup(Locale locale, String groupName, List<String> rowErrors) {
        Optional<Group> optionalGroup = groupService.getByNumber(groupName);
        if (!optionalGroup.isPresent()) {
            rowErrors.add(messageSource.getMessage(StudentValidatorImpl.GROUP_DOES_NOT_EXIST, new Object[]{groupName}, locale));
        }
        return optionalGroup.orElse(null);
    }
}
