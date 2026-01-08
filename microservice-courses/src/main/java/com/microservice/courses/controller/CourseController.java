package com.microservice.courses.controller;

import com.microservice.courses.entitiy.Course;
import com.microservice.courses.services.CourseService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping
    public void save(@RequestBody Course course){
        courseService.save(course);
    }

    @GetMapping
    public ResponseEntity<?> findAll(){
        return ResponseEntity.ok(courseService.findAll());
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        return ResponseEntity.ok(courseService.findById(id));
    }

    // Endpoint donde el usuario va a realizar una peticion y este va a consultar para obtener
    // los estudiantes del otro microservicio de students.
    @CircuitBreaker(name = "studentsCB", fallbackMethod = "fallbackGetStudentsByIdCourse")
    @GetMapping("/search-students/{idCourse}")
    public ResponseEntity<?> findStudentsByIdCourse(@PathVariable Long idCourse){
        return ResponseEntity.ok(courseService.findStudentsByIdCourse(idCourse));
    }

    private ResponseEntity<?> fallbackGetStudentsByIdCourse(Long idCourse, RuntimeException exception){
        String errorMsg = "Fallo al obtener los estudiantes del curso: "+idCourse+". Motivo " + exception.getMessage();
        return new ResponseEntity<>(errorMsg, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
