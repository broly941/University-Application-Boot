package com.loya.devi.service.Impl.fileReader;

import com.loya.devi.controller.response.ValidationStatus;
import com.loya.devi.funcInterface.TriFunction;
import com.loya.devi.service.interfaces.CSVParserService;
import com.loya.devi.service.interfaces.FileService;
import com.loya.devi.service.interfaces.XLSXParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * Business Logic Excel File Class
 * <p>
 * The class is engaged in parsing, validating
 * and saving records from a file in the database.
 *
 * @author DEVIAPHAN
 */
@Service
public class FileServiceImpl implements FileService {

    private static final String FORMAT_NOT_SUPPORTED = "FORMAT_NOT_SUPPORTED";
    private static final String XLSX = "xlsx";
    private static final String CSV = "csv";

    @Autowired
    MessageSource messageSource;

    @Autowired
    CSVParserService csvParserService;

    @Autowired
    XLSXParserService xlsxParserService;

    private static final String UNABLE_TO_UPLOAD_EMPTY_FILE = "UNABLE_TO_UPLOAD_EMPTY_FILE";

    /**
     * The method accepts a file and returns the status
     * of successfully completed validation and storage
     * or a list of errors that need to be fixed.
     *
     * @param locale of messages
     * @param file   input
     * @param save   Method for file save
     * @return validation status
     * @throws IOException if an exception occurred in the file parsing
     */
    @Override
    public ValidationStatus parse(Locale locale, InputStream file, String fileExtension, TriFunction<ConcurrentHashMap<Integer, List<Object>>, ConcurrentHashMap<Integer, Object>, Locale, ValidationStatus> validation, BiConsumer<ConcurrentHashMap<Integer, Object>, Locale> save) throws IOException {
        if (file.available() == 0) {
            throw new EntityNotFoundException(messageSource.getMessage(UNABLE_TO_UPLOAD_EMPTY_FILE, new Object[]{}, locale));
        } else {
            return getStatus(locale, file, fileExtension, validation, save);
        }
    }

    private ValidationStatus getStatus(Locale locale, InputStream file, String fileExtension, TriFunction<ConcurrentHashMap<Integer, List<Object>>, ConcurrentHashMap<Integer, Object>, Locale, ValidationStatus> validation, BiConsumer<ConcurrentHashMap<Integer, Object>, Locale> save) throws IOException {
        ConcurrentHashMap<Integer, List<Object>> parsedEntities = parse(locale, file, fileExtension);
        ConcurrentHashMap<Integer, Object> validEntities = new ConcurrentHashMap<>();
        ValidationStatus validationStatus = validation.apply(parsedEntities, validEntities, locale);
        if (validationStatus.isValid()) {
            save.accept(validEntities, locale);
        }
        return validationStatus;
    }

    private ConcurrentHashMap<Integer, List<Object>> parse(Locale locale, InputStream file, String fileExtension) throws IOException {
        ConcurrentHashMap<Integer, List<Object>> parsedEntities;
        if (fileExtension.equals(XLSX)) {
            parsedEntities = xlsxParserService.parseXlsx(file, locale);
        } else if (fileExtension.equals(CSV)) {
            parsedEntities = csvParserService.parseCsv(file, locale);
        } else {
            throw new EntityNotFoundException(messageSource.getMessage(FORMAT_NOT_SUPPORTED, new Object[]{fileExtension}, locale));
        }
        return parsedEntities;
    }

    /**
     * method return map if it not empty or throw exception
     *
     * @param locale         of message
     * @param parsedEntities map of parsed entities
     * @return map of parsed entities
     */
    @Override
    public ConcurrentHashMap<Integer, List<Object>> getIfNotEmpty(Locale locale, ConcurrentHashMap<Integer, List<Object>> parsedEntities) {
        if (parsedEntities.isEmpty()) {
            throw new EntityNotFoundException(messageSource.getMessage(UNABLE_TO_UPLOAD_EMPTY_FILE, new Object[]{}, locale));
        } else {
            return parsedEntities;
        }
    }
}