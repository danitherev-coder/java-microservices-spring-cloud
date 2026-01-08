package com.microservice.courses.client;

import com.microservice.courses.dto.StudentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "microservice-students")
public interface StudentClient {
    @GetMapping("/api/v1/students/search-by-course/{idCourse}")
    List<StudentDto> findAllStudentsByCourse(@PathVariable Long idCourse);
}
