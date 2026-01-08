package com.microservice.students.service;

import com.microservice.students.entity.Student;

import java.util.List;

public interface StudentService {

    List<Student> findAll();
    Student findById(Long id);
    void save(Student student);
    List<Student> findByIdCourse(Long courseId);
}
