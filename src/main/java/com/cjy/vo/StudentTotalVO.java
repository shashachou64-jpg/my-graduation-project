package com.cjy.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentTotalVO {
    private Long totalStudent;
    private Long manStudent;
    private Long womanStudent;
    private List<CollegeStudentVO> collegeStudentList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CollegeStudentVO {
        private String collegeName;
        private Long collegeStudent;
    }
    
}
