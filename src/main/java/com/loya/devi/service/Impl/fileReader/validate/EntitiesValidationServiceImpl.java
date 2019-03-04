package com.loya.devi.service.Impl.fileReader.validate;

import com.loya.devi.controller.response.ValidationStatus;
import com.loya.devi.service.interfaces.EntitiesValidationService;
import com.loya.devi.service.interfaces.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The class containing methods for validation.
 *
 * @author DEVIAPHAN on 14.01.2019
 * @project university
 */
@Service
public class EntitiesValidationServiceImpl implements EntitiesValidationService {
    private static final String TIMED_OUT = "Timed_out";
    private static final String SOME_TYPE_IS_NOT_A_STRING = "SOME_TYPE_IS_NOT_A_STRING";
    private static final String REQUIRED_COLUMN_MISSING = "Required_Column_Missing";
    private static final String ROW = "ROW";

    @Autowired
    private MessageSource messageSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);

    /**
     * If the value in the collection satisfies the predicate condition, an error is added.
     *
     * @param valueIsStringPredicate     predicate checks if all elements are string
     * @param hasRequiredColumnPredicate checks if all columns are required
     * @param rowErrors                  list of errors in row
     * @param value                      list of check items
     * @param locale                     of message
     * @return answer meets the conditions or not
     */
    @Override
    public boolean isValueStringAndHasReqColumn(Predicate<List<Object>> valueIsStringPredicate, Predicate<List<Object>> hasRequiredColumnPredicate, List<String> rowErrors, List<Object> value, Locale locale) {
        if (hasRequiredColumnPredicate.test(value)) {
            rowErrors.add(messageSource.getMessage(REQUIRED_COLUMN_MISSING, new Object[]{}, locale));
            return false;
        } else if (!valueIsStringPredicate.test(value)) {
            rowErrors.add(messageSource.getMessage(SOME_TYPE_IS_NOT_A_STRING, new Object[]{}, locale));
            return false;
        } else {
            return true;
        }
    }

    /**
     * method fill validation status depending valid status or not
     *
     * @param validationStatus of map
     * @param key              of row
     * @param validEntity      of map
     * @param locale           of message
     * @param rowErrors        of map
     * @param validEntities    - map of valid entities of file
     */
    @Override
    public void fillValidationStatus(ValidationStatus validationStatus, int key, Object validEntity, Locale locale, List<String> rowErrors, ConcurrentHashMap<Integer, Object> validEntities) {
        if (rowErrors.isEmpty()) {
            validationStatus.validRowInc();
            validEntities.put(key, validEntity);
        } else {
            String rowLabel = messageSource.getMessage(ROW, new Object[]{key}, locale);
            validationStatus.errorRowInc();
            validationStatus.append(rowLabel + rowErrors);
        }
    }

    /**
     * method return executor service by count core your system
     *
     * @return ExecutorService
     */
    @Override
    public ExecutorService getExecutorService() {
        int countCore = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(countCore);
    }

    /**
     * the method splits the collection into threads and performs validation, then returns the status of the validation.
     *
     * @param parsedEntities of file
     * @param validEntities  is valid entities in parsed file
     * @param locale         of message
     * @param validator      of method for validate
     * @return validation status
     */
    @Override
    public ValidationStatus validateParsedEntities(ConcurrentHashMap<Integer, List<Object>> parsedEntities, ConcurrentHashMap<Integer, Object> validEntities, Locale locale, Function<ValidationParameters, Void> validator) {
        ValidationStatus validationStatus = getValidationStatus(parsedEntities);
        CopyOnWriteArraySet<String> duplicateSet = new CopyOnWriteArraySet<>();
        ExecutorService executor = getExecutorService();
        List<? extends Callable<Void>> tasks = setTasks(validator, parsedEntities, validationStatus, validEntities, locale, duplicateSet);

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            LOGGER.info(messageSource.getMessage(TIMED_OUT, new Object[]{}, locale));
            executor.shutdownNow();
        }

        validationStatus.sortErrors();

        return validationStatus;
    }

    private List<Callable<Void>> setTasks(Function<ValidationParameters, Void> validator, ConcurrentHashMap<Integer, List<Object>> parsedEntities, ValidationStatus validationStatus, ConcurrentHashMap<Integer, Object> validEntities, Locale locale, CopyOnWriteArraySet<String> duplicateSet) {
        return parsedEntities.entrySet().stream()
                .map(m -> (Callable<Void>) () -> {
                    ValidationParameters validationParameters = new ValidationParameters(m.getKey(), m.getValue(), validEntities, locale, validationStatus, duplicateSet);
                    validator.apply(validationParameters);
                    return null;
                }).collect(Collectors.toList());
    }

    private ValidationStatus getValidationStatus(ConcurrentHashMap<Integer, List<Object>> parsedEntities) {
        ValidationStatus validationStatus = new ValidationStatus();
        validationStatus.setRowCount(parsedEntities.size());
        return validationStatus;
    }
}