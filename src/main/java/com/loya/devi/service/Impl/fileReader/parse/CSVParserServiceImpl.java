package com.loya.devi.service.Impl.fileReader.parse;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature;
import com.loya.devi.service.Impl.fileReader.FileServiceImpl;
import com.loya.devi.service.interfaces.CSVParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Class parse csv file and return map of entities
 *
 * @author DEVIAPHAN on 21.01.2019
 * @project university
 */
@Service
public class CSVParserServiceImpl implements CSVParserService {

    private static final String SEMICOLON = ";";

    private final String numericStrRegex = "^(?:(?:\\-{1})?\\d+(?:\\.{1}\\d+)?)$";
    private final String booleanRegex = "^(?i)(true|false)$";
    private final String spacesRegex = "^\\s\\s+$";

    private final Predicate<String> boolPredicate = (str) -> str.matches(booleanRegex);
    private final Predicate<String> numStrPredicate = (str) -> str.matches(numericStrRegex);
    private final Predicate<String> emptyPredicate = (str) -> str.isEmpty() || str.matches(spacesRegex);

    @Autowired
    MessageSource messageSource;

    @Autowired
    FileServiceImpl fileService;

    private CsvMapper mapper;

    /**
     * Construct for CSCParserService
     */
    @PostConstruct
    public void init() {
        mapper = new CsvMapper();
        mapper.enable(Feature.WRAP_AS_ARRAY);
    }

    /**
     * method parse csv file and return map
     *
     * @param file   for init db
     * @param locale of message
     * @return map of parsed entities
     * @throws IOException if throw exception in parse
     */
    @Override
    public ConcurrentHashMap<Integer, List<Object>> parseCsv(InputStream file, Locale locale) throws IOException {
        int rowIndex = 1;
        ConcurrentHashMap<Integer, List<Object>> parsedEntities = new ConcurrentHashMap<>();
        MappingIterator<String[]> it = mapper.readerFor(String[].class).readValues(file);
        it.next();
        while (it.hasNext()) {
            String[] values = it.next()[0].split(SEMICOLON);
            List<Object> objectList = getParameterizedList(values);
            parsedEntities.put(rowIndex++, objectList);
        }
        return fileService.getIfNotEmpty(locale, parsedEntities);
    }

    private List<Object> getParameterizedList(String[] values) {
        List<Object> objectList = new ArrayList<>();
        for (String value : values) {
            if (numStrPredicate.test(value)) {
                objectList.add(Double.valueOf(value));
            } else if (boolPredicate.test(value)) {
                objectList.add(Boolean.valueOf(value));
            } else if (emptyPredicate.test(value)) {
                objectList.add(new Object());
            } else {
                objectList.add(value);
            }
        }
        return objectList;
    }
}
