package com.loya.devi.service.interfaces;

import com.loya.devi.controller.response.ValidationStatus;
import com.loya.devi.funcInterface.TriFunction;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * @author DEVIAPHAN on 03.01.2019
 * @project university
 */
public interface FileService {
    ValidationStatus parse(Locale locale, InputStream file, String fileExtension, TriFunction<ConcurrentHashMap<Integer, List<Object>>, ConcurrentHashMap<Integer, Object>, Locale, ValidationStatus> validation, BiConsumer<ConcurrentHashMap<Integer, Object>, Locale> save) throws IOException;

    ConcurrentHashMap<Integer, List<Object>> getIfNotEmpty(Locale locale, ConcurrentHashMap<Integer, List<Object>> parsedEntities);
}
