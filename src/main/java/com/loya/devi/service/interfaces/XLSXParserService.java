package com.loya.devi.service.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DEVIAPHAN on 21.01.2019
 * @project university
 */
public interface XLSXParserService {
    ConcurrentHashMap<Integer, List<Object>> parseXlsx(InputStream file, Locale locale) throws IOException;
}
