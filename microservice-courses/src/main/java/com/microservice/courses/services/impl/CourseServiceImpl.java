package com.microservice.courses.services.impl;

import com.microservice.courses.client.StudentClient;
import com.microservice.courses.dto.StudentDto;
import com.microservice.courses.entitiy.Course;
import com.microservice.courses.htpp.response.StudentByCourseResponse;
import com.microservice.courses.repository.CourseRepository;
import com.microservice.courses.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentClient studentClient;

    @Override
    public List<Course> findAll() {
        return ((List<Course>) courseRepository.findAll());
    }

    @Override
    public Course findById(Long id) {
        return courseRepository.findById(id).orElseThrow();
    }

    @Override
    public void save(Course course) {
        courseRepository.save(course);
    }

    @Override
    public StudentByCourseResponse findStudentsByIdCourse(Long idCourse) {
        // Consultar el Course
        Course course = courseRepository.findById(idCourse).orElse(new Course());
        // Obtener los students del microservicio students
        List<StudentDto> studentDtoList = studentClient.findAllStudentsByCourse(idCourse);

        return StudentByCourseResponse.builder()
                .courseName(course.getName())
                .teacher(course.getTeacher())
                .studentDtoList(studentDtoList)
                .build();
    }
}
