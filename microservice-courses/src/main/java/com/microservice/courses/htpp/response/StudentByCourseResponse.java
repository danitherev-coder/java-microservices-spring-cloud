package com.microservice.courses.htpp.response;

import com.microservice.courses.dto.StudentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentByCourseResponse {
    private String courseName;
    private String teacher;
    private List<StudentDto> studentDtoList;
}
