package com.loya.devi.service.interfaces;

import com.loya.devi.controller.response.ValidationStatus;
import com.loya.devi.service.Impl.fileReader.validate.ValidationParameters;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author DEVIAPHAN on 15.01.2019
 * @project university
 */
public interface EntitiesValidationService {
    boolean isValueStringAndHasReqColumn(Predicate<List<Object>> valueIsStringPredicate, Predicate<List<Object>> HasRequiredColumnPredicate, List<String> rowErrors, List<Object> value, Locale locale);

    void fillValidationStatus(ValidationStatus validationStatus, int key, Object validEntity, Locale locale, List<String> rowErrors, ConcurrentHashMap<Integer, Object> validEntities);

    ExecutorService getExecutorService();

    ValidationStatus validateParsedEntities(ConcurrentHashMap<Integer, List<Object>> parsedEntities, ConcurrentHashMap<Integer, Object> validEntities, Locale locale, Function<ValidationParameters, Void> validator);
}
