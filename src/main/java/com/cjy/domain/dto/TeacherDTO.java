package com.cjy.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教师信息DTO，用于前端展示
 * 包含教师基本信息以及关联的学院、职位名称
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDTO {
    private String id;
    private String name;
    private String collegeId;
    private String collegeName;
    private String gender;
    private String positionId;
    private String positionName;
}

