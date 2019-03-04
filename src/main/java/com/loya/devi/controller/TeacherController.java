package com.loya.devi.controller;

import com.loya.devi.controller.component.DTOConverter;
import com.loya.devi.controller.request.PageRequestParameters;
import com.loya.devi.controller.request.PaginationPage;
import com.loya.devi.controller.request.StudentTeacherFilter;
import com.loya.devi.controller.response.ValidationStatus;
import com.loya.devi.entity.Teacher;
import com.loya.devi.entity.dto.TeacherDTO;
import com.loya.devi.service.interfaces.FileService;
import com.loya.devi.service.interfaces.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Processes the request and returns the response as JSON.
 *
 * @author DEVIAPHAN
 */
@RestController
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    TeacherService teacherService;

    @Autowired
    FileService fileService;

    @Autowired
    DTOConverter dtoConverter;

    /**
     * method return paginated list by page/number or entity as result of filtering
     *
     * @param page      of pagination
     * @param number    of pagination
     * @param firstName for filtering of pagination result
     * @param lastName  for filtering of pagination result
     * @param locale    of message
     * @return paginated list or entity
     */
    @GetMapping
    public Page<TeacherDTO> find(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer number,
                                 @RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName, Locale locale) {
        return teacherService.getByFilter(new PageRequestParameters<>(new PaginationPage(page, number), new StudentTeacherFilter(firstName, lastName)), locale).map(this::convertToDto);
    }

    /**
     * method return teacher by id
     *
     * @param id of teacher
     * @return teacher entity by ID in the database.
     */
    @GetMapping("/{id}")
    public TeacherDTO getById(@PathVariable Long id, Locale locale) {
        return convertToDto(teacherService.getById(id, locale));
    }

    /**
     * method return all teachers by group id
     *
     * @param id     of group
     * @param locale of message
     * @return all teachers by group id
     */
    @GetMapping("/getByGroupId/{id}")
    public List<TeacherDTO> getTeachersOfGroupById(@PathVariable Long id, Locale locale) {
        return teacherService.getTeachersOfGroupById(id, locale).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * method save teacher
     *
     * @param teacherDTO entity
     * @param locale     of message
     * @return added teacher entity in the database.
     */
    @PostMapping
    public TeacherDTO save(@RequestBody TeacherDTO teacherDTO, Locale locale) {
        return convertToDto(teacherService.save(convertToEntity(teacherDTO), locale));
    }

    /**
     * update teacher by id
     *
     * @param teacherDTO entity
     * @param id         of teacher
     * @param locale     of message
     * @return updated group entity in the database.
     */
    @PutMapping("/{id}")
    public TeacherDTO updateById(@RequestBody TeacherDTO teacherDTO, @PathVariable Long id, Locale locale) {
        Teacher teacher = teacherService.updateById(convertToEntity(teacherDTO), id, locale);
        return convertToDto(teacher);
    }

    /**
     * method delete teacher by id
     *
     * @param id     the teacher entity to be removed from the database
     * @param locale of message
     */
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id, Locale locale) {
        teacherService.deleteById(id, locale);
    }

    /**
     * method save entities in database from excel file
     *
     * @param file   excel
     * @param locale of message
     * @return validation status
     * @throws IOException if an exception occurred in the fileReader parsing
     */
    @PostMapping("/upload")
    public ValidationStatus addGroupsToTeacher(@RequestParam(value = "file") MultipartFile file, Locale locale) throws IOException {
        return fileService.parse(locale, file.getInputStream(), file.getOriginalFilename().split("\\.")[1], teacherService::validate, teacherService::save);
    }

    /**
     * method return all sorted teachers using oracle package
     *
     * @return All entity in the database.
     */
    @GetMapping("/sort-asc")
    public List<TeacherDTO> getSortedTeachers() {
        return teacherService.getSortedTeachers().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * method return all sorted revert teachers using oracle package
     *
     * @return All entity in the database.
     */
    @GetMapping("/sort-desc")
    public List<TeacherDTO> getSortedRevertTeachers() {
        return teacherService.getSortedRevertTeachers().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private TeacherDTO convertToDto(Teacher teacher) {
        return (TeacherDTO) dtoConverter.convert(teacher, TeacherDTO.class);
    }

    private Teacher convertToEntity(TeacherDTO teacherDTO) {
        return (Teacher) dtoConverter.convert(teacherDTO, Teacher.class);
    }
}
