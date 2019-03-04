package com.loya.devi.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The class contains information about the file and validation status.
 *
 * @author DEVIAPHAN on 10.01.2019
 * @project university
 */
public class ValidationStatus {

    private int rowCount = 0;

    private AtomicInteger validRow = new AtomicInteger(0);

    private AtomicInteger errorsCount = new AtomicInteger(0);

    private CopyOnWriteArrayList<String> errors;

    public ValidationStatus() {
        errors = new CopyOnWriteArrayList<>();
    }

    public void append(String value) {
        errors.add(value);
    }

    public void validRowInc() {
        validRow.getAndIncrement();
    }

    public void errorRowInc() {
        errorsCount.getAndIncrement();
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    @JsonIgnore
    public boolean isValid() {
        return errors.isEmpty();
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getValidRow() {
        return validRow.get();
    }

    public int getErrorsCount() {
        return errorsCount.get();
    }

    public List<String> getErrors() {
        return errors;
    }

    public void sortErrors() {
        Collections.sort(errors);
    }
}
