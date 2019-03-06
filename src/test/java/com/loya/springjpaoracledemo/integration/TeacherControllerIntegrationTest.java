package com.loya.springjpaoracledemo.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.loya.devi.entity.Teacher;
import com.loya.springjpaoracledemo.config.DataConfigTest;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test for Controller Teacher Class
 *
 * @author DEVIAPHAN
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DataConfigTest.class)
@WebAppConfiguration
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
@DatabaseSetups({
        @DatabaseSetup("/xml/teachers.xml"),
        @DatabaseSetup("/xml/groups.xml"),
        @DatabaseSetup("/xml/groupteacher.xml")
})
public class TeacherControllerIntegrationTest {

    private static final String ACCEPT_LANGUAGE = "Accept-language";
    private static final String EN = "en-US";
    private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";

    private static final String TEACHERS_ID = "/teachers/{id}";
    private static final String TEACHERS = "/teachers";
    private static final String $_ID = "$.id";
    private static final String $_FIRST_NAME = "$.firstName";
    private static final String $_LAST_NAME = "$.lastName";
    private static final String $_2_ID = "$[2].id";
    private static final String $_2_FIRST_NAME = "$[2].firstName";
    private static final String $_2_LAST_NAME = "$[2].lastName";
    private static final String ROB = "Rob";
    private static final String STARK = "Stark";
    private static final String YIP = "Yip";
    private static final String MAN = "Man";
    private static final String ILYA = "Ilya";
    private static final String KORZHAVIN = "Korzhavin";

    private static final String TEACHERS_UPLOAD = "/teachers/upload";
    private static final String FILE = "file";
    private static final String FILE_XLSX = "file/xlsx";
    private static final String SUCCESS_XLSX_FILE = "addGroupsToTeacher_Success.xlsx";
    private static final String UNSUCCESS_XLSX_FILE = "addGroupsToTeacher_Unsuccessful.xlsx";
    private static final String SUCCESS_CSV_FILE = "addGroupsToTeacher_Success.csv";
    private static final String UNSUCCESS_CSV_FILE = "addGroupsToTeacher_Unsuccessful.csv";
    private static final String ADD_GROUPS_TO_TEACHERS_SUC = "{\"rowCount\":3,\"validRow\":3,\"errorsCount\":0,\"errors\":[]}";
    private static final String ADD_GROUPS_TO_TEACHERS_UNSUC = "{\"rowCount\":3,\"validRow\":0,\"errorsCount\":3,\"errors\":[\"Row 1: [teacher already exists]\",\"Row 2: [some type is not a string]\",\"Row 3: [the required number of columns is missing]\"]}";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    /**
     * addGroupsToTeacher_Failed mockMvc
     */
    @Before
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

//    /**
//     * Will return an all records from tested db
//     *
//     * @throws Exception in json parse or MockMvc.perform
//     */
//    @Test
//    public void getAll() throws Exception {
//        mockMvc.perform(get(TEACHERS)
//                .header(ACCEPT_LANGUAGE, EN)
//        )
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
//                .andExpect(jsonPath($_2_ID, is(3)))
//                .andExpect(jsonPath($_2_FIRST_NAME, is(ROB)))
//                .andExpect(jsonPath($_2_LAST_NAME, is(STARK)));
//    }

    /**
     * Will return a record by ID from tested db
     *
     * @throws Exception in json parse or MockMvc.perform
     */
    @Test
    public void getById() throws Exception {
        mockMvc.perform(get(TEACHERS_ID, 3)
                .header(ACCEPT_LANGUAGE, EN)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
                .andExpect(jsonPath($_ID, is(3)))
                .andExpect(jsonPath($_FIRST_NAME, is(ROB)))
                .andExpect(jsonPath($_LAST_NAME, is(STARK)));
    }

    /**
     * Will save a record in tested db
     *
     * @throws Exception in json parse or MockMvc.perform
     */
    @Test
    public void save() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setFirstName(YIP);
        teacher.setLastName(MAN);

        mockMvc.perform(post(TEACHERS)
                .header(ACCEPT_LANGUAGE, EN)
                .contentType(APPLICATION_JSON_CHARSET_UTF_8)
                .content(json(teacher))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
                .andExpect(jsonPath($_ID, is(7)))
                .andExpect(jsonPath($_FIRST_NAME, is(YIP)))
                .andExpect(jsonPath($_LAST_NAME, is(MAN)));
    }

    /**
     * Will update a record by ID from tested db
     *
     * @throws Exception in json parse or MockMvc.perform
     */
    @Test
    public void update() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setFirstName(ILYA);
        teacher.setLastName(KORZHAVIN);

        mockMvc.perform(put(TEACHERS_ID, 2)
                .header(ACCEPT_LANGUAGE, EN)
                .contentType(APPLICATION_JSON_CHARSET_UTF_8)
                .content(json(teacher))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
                .andExpect(jsonPath($_ID, is(2)))
                .andExpect(jsonPath($_FIRST_NAME, is(ILYA)))
                .andExpect(jsonPath($_LAST_NAME, is(KORZHAVIN)));
    }

    /**
     * Will delete a record by ID from tested db
     *
     * @throws Exception in json parse or MockMvc.perform
     */
    @Test
    public void remove() throws Exception {
        mockMvc.perform(delete(TEACHERS_ID, 3)
                .header(ACCEPT_LANGUAGE, EN)
        )
                .andExpect(status().isOk());
    }

    /**
     * Will save a new entity and return validation status
     *
     * @throws Exception in json parse or MockMvc.perform
     */
    @Test
    public void addGroupsToTeacher_xlsx_Success() throws Exception {
        MockMultipartFile file = getMultipartFile(SUCCESS_XLSX_FILE);
        mockMvc.perform(multipart(TEACHERS_UPLOAD)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string(ADD_GROUPS_TO_TEACHERS_SUC));
    }

    /**
     * Will save a new entity and return validation status
     *
     * @throws Exception in json parse or MockMvc.perform
     */
    @Test
    public void addGroupsToTeacher_xlsx_Unsuccessful() throws Exception {
        MockMultipartFile file = getMultipartFile(UNSUCCESS_XLSX_FILE);
        mockMvc.perform(multipart(TEACHERS_UPLOAD)
                .file(file)
                .header(ACCEPT_LANGUAGE, EN)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string(ADD_GROUPS_TO_TEACHERS_UNSUC));
    }

    /**
     * Will save a new entity and return validation status
     *
     * @throws Exception in json parse or MockMvc.perform
     */
    @Test
    public void addGroupsToTeacher_csv_Success() throws Exception {
        MockMultipartFile file = getMultipartFile(SUCCESS_CSV_FILE);
        mockMvc.perform(multipart(TEACHERS_UPLOAD)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string(ADD_GROUPS_TO_TEACHERS_SUC));
    }

    /**
     * Will save a new entity and return validation status
     *
     * @throws Exception in json parse or MockMvc.perform
     */
    @Test
    public void addGroupsToTeacher_csv_Unsuccessful() throws Exception {
        MockMultipartFile file = getMultipartFile(UNSUCCESS_CSV_FILE);
        mockMvc.perform(multipart(TEACHERS_UPLOAD)
                .file(file)
                .header(ACCEPT_LANGUAGE, EN)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string(ADD_GROUPS_TO_TEACHERS_UNSUC));
    }

    private MockMultipartFile getMultipartFile(String fileName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
        FileInputStream input = new FileInputStream(file);
        return new MockMultipartFile(FILE,
                file.getName(), FILE_XLSX, IOUtils.toByteArray(input));
    }

    private String json(Object o) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(o);
    }
}