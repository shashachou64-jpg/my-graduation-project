package com.cjy.domain;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("course_with_students")
public class CourseWithStudents {
    private Integer courseId;
    private String studentNumber;
}
