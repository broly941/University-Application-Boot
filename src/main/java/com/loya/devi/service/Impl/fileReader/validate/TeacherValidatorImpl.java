package com.loya.devi.service.Impl.fileReader.validate;

import com.loya.devi.entity.Group;
import com.loya.devi.entity.Teacher;
import com.loya.devi.service.interfaces.EntitiesValidationService;
import com.loya.devi.service.interfaces.GroupService;
import com.loya.devi.service.interfaces.TeacherService;
import com.loya.devi.service.interfaces.TeacherValidator;
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
public class TeacherValidatorImpl implements TeacherValidator {
    private static final String DUPLICATE = "DUPLICATE";
    @Autowired
    private TeacherService teacherService;

    @Autowired
    private GroupService groupService;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    EntitiesValidationService entitiesValidationService;

    @Autowired
    private MessageSource messageSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);

    private static final String TEACHER_ALREADY_EXISTS = "TEACHER_ALREADY_EXISTS";
    private static final String GROUP_DOES_NOT_EXIST = "GROUP_DOES_NOT_EXIST";

    private static final Predicate<List<Object>> leastRequiredColumnPredicate = value -> value.size() < 3;
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
        String firstName = null;
        String lastName = null;
        List<Group> groupList = null;
        List<String> rowErrors = new ArrayList<>();
        List<Object> values = parameters.getValue();
        Locale locale = parameters.getLocale();
        if (entitiesValidationService.isValueStringAndHasReqColumn(instanceStringPredicate, leastRequiredColumnPredicate, rowErrors, values, locale)) {
            firstName = values.get(0).toString();
            lastName = values.get(1).toString();
            validateTeacher(firstName, lastName, rowErrors, locale);
            groupList = validateGroup(values, rowErrors, locale);
            validateDuplicate(parameters.getDuplicateSet(), firstName, lastName, rowErrors, locale);
        }
        entitiesValidationService.fillValidationStatus(parameters.getValidationStatus(), parameters.getKey(), new Teacher(firstName, lastName, groupList), locale, rowErrors, parameters.getValidEntities());
        getRuntimeStatistic(startTime);
        return null;
    }

    private void getRuntimeStatistic(long startTime) {
        long time = System.currentTimeMillis() - startTime;
        LOGGER.info(Thread.currentThread().getName() + ": " + time + " millis");
    }

    private void validateDuplicate(CopyOnWriteArraySet<String> duplicateSet, String firstName, String lastName, List<String> rowErrors, Locale locale) {
        if (!duplicateSet.add(firstName + " " + lastName)) {
            rowErrors.add(messageSource.getMessage(DUPLICATE, new Object[]{}, locale));
        }
    }

    private void validateTeacher(String firstName, String lastName, List<String> rowErrors, Locale locale) {
        Optional<Teacher> optionalTeacher = teacherService.getTeacherByName(firstName, lastName);
        if (optionalTeacher.isPresent()) {
            rowErrors.add(messageSource.getMessage(TEACHER_ALREADY_EXISTS, new Object[]{}, locale));
        }
    }

    private List<Group> validateGroup(List<Object> values, List<String> rowErrors, Locale locale) {
        List<Group> groupList = new ArrayList<>();
        for (int i = 2; i < values.size(); i++) {
            Optional<Group> group = groupService.getByNumber(values.get(i).toString());
            if (group.isPresent()) {
                groupList.add(group.get());
            } else {
                rowErrors.add(messageSource.getMessage(GROUP_DOES_NOT_EXIST, new Object[]{values.get(i).toString()}, locale));
            }
        }
        return groupList;
    }
}
