package com.cjy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("teacher_with_course")
public class TeacherWithCourse {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long teacherId;
    private Long courseId;
    
}
