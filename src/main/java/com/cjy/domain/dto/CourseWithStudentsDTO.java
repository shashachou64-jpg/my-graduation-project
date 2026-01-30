package com.cjy.domain.dto;

import java.util.List;

import com.cjy.domain.Course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseWithStudentsDTO {
    private Course course;
    private String collegeName;
    private List<String> studentsNumberList;
}
