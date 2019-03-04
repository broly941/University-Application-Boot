package com.loya.devi.service.Impl.fileReader.parse;

import com.loya.devi.service.Impl.fileReader.FileServiceImpl;
import com.loya.devi.service.interfaces.XLSXParserService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class parse csv file and return map of entities
 *
 * @author DEVIAPHAN on 21.01.2019
 * @project university
 */
@Service
public class XLSXParserServiceImpl implements XLSXParserService {

    @Autowired
    MessageSource messageSource;

    @Autowired
    FileServiceImpl fileService;

    /**
     * parse excel file and return map
     *
     * @param file   for init db
     * @param locale of message
     * @return map of parsed entities
     * @throws IOException if throw exception in parse
     */
    @Override
    public ConcurrentHashMap<Integer, List<Object>> parseXlsx(InputStream file, Locale locale) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        workbook.close();
        return getMap(rowIterator, locale);
    }

    private ConcurrentHashMap<Integer, List<Object>> getMap(Iterator<Row> rowIterator, Locale locale) {
        int rowIndex = 0;
        ConcurrentHashMap<Integer, List<Object>> parsedEntities = new ConcurrentHashMap<>();
        rowIterator.next();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();

            List<Object> lineList = new ArrayList<>();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                rowIndex = row.getRowNum();

                switch (cell.getCellType()) {
                    case STRING:
                        lineList.add(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        lineList.add(cell.getNumericCellValue());
                        break;
                    case BOOLEAN:
                        lineList.add(cell.getBooleanCellValue());
                        break;
                }
            }
            parsedEntities.put(rowIndex, lineList);
        }
        return fileService.getIfNotEmpty(locale, parsedEntities);
    }
}
