package com.cjy.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherTotalVO {
    private Long totalTeacher;
    private Long manTeacher;
    private Long womanTeacher;
    private List<CollegeTeacherVO> collegeTeacherList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CollegeTeacherVO {
        private String collegeName;
        private Long collegeTeacher;
    }
}
