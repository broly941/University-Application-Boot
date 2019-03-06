package com.loya.springjpaoracledemo.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.loya.devi.entity.Group;
import com.loya.springjpaoracledemo.config.DataConfigTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test for Controller Group Class
 *
 * @author DEVIAPHAN on 17.12.2018
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DataConfigTest.class)
@WebAppConfiguration
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
@DatabaseSetups({
        @DatabaseSetup("/xml/teachers.xml"),
        @DatabaseSetup("/xml/groups.xml")
})
public class GroupControllerIntegrationTest {

    private static final String ACCEPT_LANGUAGE = "Accept-language";
    private static final String EN = "en";
    private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";

    private static final String GROUPS = "/groups";
    private static final String GROUPS_ID = "/groups/{id}";
    private static final String $_ID = "$.id";
    private static final String $_NUMBER = "$.number";
    private static final String CURATOR_ID = "curatorId";
    private static final String TEACHER_ID_LIST = "teacherIdList";
    private static final String $_2_ID = "$[2].id";
    private static final String $_2_NUMBER = "$[2].number";
    private static final String POIT_21 = "POIT-21";
    private static final String POIT_161 = "POIT-161";
    private static final String POIT_12 = "POIT-12";

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

    /**
     * Will return an all records from tested db
     *
     * @throws Exception in json parse or MockMvc.perform
     */
    @Test
    public void getAll() throws Exception {
        mockMvc.perform(get(GROUPS)
                .header(ACCEPT_LANGUAGE, EN)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
                .andExpect(jsonPath($_2_ID, is(3)))
                .andExpect(jsonPath($_2_NUMBER, is(POIT_21)));
    }

    /**
     * Will return a record by ID from tested db
     *
     * @throws Exception in json parse or MockMvc.perform
     */
    @Test
    public void getById() throws Exception {
        mockMvc.perform(get(GROUPS_ID, 1)
                .header(ACCEPT_LANGUAGE, EN)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
                .andExpect(jsonPath($_ID, is(1)))
                .andExpect(jsonPath($_NUMBER, is(POIT_161)));
    }

    /**
     * Will save a record in tested db
     *
     * @throws Exception in json parse or MockMvc.perform
     */
    @Test
    public void save() throws Exception {
        Group group = new Group();
        group.setNumber(POIT_12);

        ResultActions performSave = mockMvc.perform(post(GROUPS)
                .header(ACCEPT_LANGUAGE, EN)
                .param(CURATOR_ID, "3")
                .param(TEACHER_ID_LIST, "3")
                .contentType(APPLICATION_JSON_CHARSET_UTF_8)
                .content(json(group))
        );

        performSave
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
                .andExpect(jsonPath($_ID, is(4)))
                .andExpect(jsonPath($_NUMBER, is(POIT_12)));
    }

    /**
     * Will update a record by ID from tested db
     *
     * @throws Exception in json parse or MockMvc.perform
     */
    @Test
    public void update() throws Exception {
        Group group = new Group();
        group.setNumber(POIT_12);

        mockMvc.perform(put(GROUPS_ID, 2)
                .header(ACCEPT_LANGUAGE, EN)
                .param(CURATOR_ID, "2")
                .param(TEACHER_ID_LIST, "3")
                .contentType(APPLICATION_JSON_CHARSET_UTF_8)
                .content(json(group))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
                .andExpect(jsonPath($_ID, is(2)))
                .andExpect(jsonPath($_NUMBER, is(POIT_12)));
    }

    /**
     * Will delete a record by ID from tested db
     *
     * @throws Exception in json parse or MockMvc.perform
     */
    @Test
    public void remove() throws Exception {
        mockMvc.perform(delete(GROUPS_ID, 1)
                .header(ACCEPT_LANGUAGE, EN)
        )
                .andExpect(status().isOk());
    }

    private String json(Object o) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(o);
    }
}