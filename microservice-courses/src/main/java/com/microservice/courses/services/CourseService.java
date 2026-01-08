package com.microservice.courses.services;

import com.microservice.courses.entitiy.Course;
import com.microservice.courses.htpp.response.StudentByCourseResponse;

import java.util.List;

public interface CourseService {

    List<Course> findAll();
    Course findById(Long id);
    void save(Course course);


    // Para comunicar con el microservicio de students
    StudentByCourseResponse findStudentsByIdCourse(Long idCourse);
}
