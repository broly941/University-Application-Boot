package com.loya.devi.controller;

import com.loya.devi.controller.component.DTOConverter;
import com.loya.devi.controller.request.PageRequestParameters;
import com.loya.devi.controller.request.PaginationPage;
import com.loya.devi.controller.request.StudentTeacherFilter;
import com.loya.devi.controller.response.ValidationStatus;
import com.loya.devi.entity.Student;
import com.loya.devi.entity.dto.StudentDTO;
import com.loya.devi.service.interfaces.FileService;
import com.loya.devi.service.interfaces.StudentService;
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
@RequestMapping("/students")
public class StudentController {

    @Autowired
    StudentService studentService;

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
    public Page<StudentDTO> find(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer number,
                                 @RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName, Locale locale) {
        return studentService.getByFilter(new PageRequestParameters<>(new PaginationPage(page, number), new StudentTeacherFilter(firstName, lastName)), locale).map(this::convertToDto);
    }


    /**
     * method return all student by group id
     *
     * @param id     of group
     * @param locale of message
     * @return all student by group id
     */
    @GetMapping("/getByGroupId/{id}")
    public List<StudentDTO> getStudentsOfGroupById(@PathVariable Long id, Locale locale) {
        return studentService.getStudentsOfGroupById(id, locale).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * method return student by id
     *
     * @param id of student
     * @return student entity by ID in the database.
     */
    @GetMapping("/{id}")
    public StudentDTO getById(@PathVariable Long id, Locale locale) {
        return convertToDto(studentService.getById(id, locale));
    }

    /**
     * method save student
     *
     * @param studentDTO entity
     * @param groupId    of Group
     * @return added student entity in the database.
     */
    @PostMapping
    public StudentDTO save(@RequestBody StudentDTO studentDTO, @RequestParam Long groupId, Locale locale) {
        return convertToDto(studentService.save(convertToEntity(studentDTO), groupId, locale));
    }

    /**
     * method update student by id
     *
     * @param studentDTO entity
     * @param studentId  of student
     * @param groupId    of group
     * @return updated student entity in the database.
     */
    @PutMapping("/{studentId}")
    public StudentDTO updateById(@RequestBody StudentDTO studentDTO, @PathVariable Long studentId, @RequestParam Long groupId, Locale locale) {
        return convertToDto(studentService.updateById(convertToEntity(studentDTO), studentId, groupId, locale));
    }

    /**
     * method delete student by id
     *
     * @param id     the student entity to be removed from the database
     * @param locale of message
     */
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id, Locale locale) {
        studentService.deleteById(id, locale);
    }

    /**
     * method save entities in database from excel file
     *
     * @param file   excel
     * @param locale of message
     * @return validation status
     * @throws IOException if an exception occurred in the file parsing
     */
    @PostMapping("/upload")
    public ValidationStatus addStudentsToGroup(@RequestParam(value = "file") MultipartFile file, Locale locale) throws IOException {
        return fileService.parse(locale, file.getInputStream(), file.getOriginalFilename().split("\\.")[1], studentService::validate, studentService::save);
    }

    /**
     * method return all sorted teachers using oracle package
     *
     * @return All entity in the database.
     */
    @GetMapping("/sort-asc")
    public List<StudentDTO> getSortedTeachers() {
        return studentService.getSortedStudents().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * method return all sorted revert students using oracle package
     *
     * @return All entity in the database.
     */
    @GetMapping("/sort-desc")
    public List<StudentDTO> getSortedRevertTeachers() {
        return studentService.getSortedRevertStudents().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private StudentDTO convertToDto(Student student) {
        return (StudentDTO) dtoConverter.convert(student, StudentDTO.class);
    }

    private Student convertToEntity(StudentDTO studentDTO) {
        return (Student) dtoConverter.convert(studentDTO, Student.class);
    }
}