package com.cjy.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseTotalVO {
    private Long totalCourse;

    List<CollegeCourseVO> collegeCourseVOList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CollegeCourseVO {
        private String collegeName;
        private Long courseCount;
    }
}
