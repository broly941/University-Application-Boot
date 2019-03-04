package com.loya.devi.service.Impl.fileReader.validate;

import com.loya.devi.controller.response.ValidationStatus;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * The class contains parameters for validation.
 *
 * @author DEVIAPHAN on 1/28/2019
 * @project university
 */
public class ValidationParameters {
    private int key;
    private List<Object> value;
    private ConcurrentHashMap<Integer, Object> validEntities;
    private Locale locale;
    private ValidationStatus validationStatus;
    private CopyOnWriteArraySet<String> duplicateSet;

    public ValidationParameters(int key, List<Object> value, ConcurrentHashMap<Integer, Object> validEntities, Locale locale, ValidationStatus validationStatus, CopyOnWriteArraySet<String> duplicateSet) {
        this.key = key;
        this.value = value;
        this.validEntities = validEntities;
        this.locale = locale;
        this.validationStatus = validationStatus;
        this.duplicateSet = duplicateSet;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public List<Object> getValue() {
        return value;
    }

    public void setValue(List<Object> value) {
        this.value = value;
    }

    public ConcurrentHashMap<Integer, Object> getValidEntities() {
        return validEntities;
    }

    public void setValidEntities(ConcurrentHashMap<Integer, Object> validEntities) {
        this.validEntities = validEntities;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(ValidationStatus validationStatus) {
        this.validationStatus = validationStatus;
    }

    public CopyOnWriteArraySet<String> getDuplicateSet() {
        return duplicateSet;
    }

    public void setDuplicateSet(CopyOnWriteArraySet<String> duplicateSet) {
        this.duplicateSet = duplicateSet;
    }
}
