package com.cjy.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {
    private Integer id;
    private String courseName;
    private Integer credit;
    private Integer maxNum;
    private Integer currentNum;
    private String collegeName;
    private List<StudentInfo> studentInfoList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StudentInfo {
        private String number;
        private String name;
    }
}
