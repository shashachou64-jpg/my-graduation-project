package com.cjy.dto;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "教师姓名不能为空")
    private String name;
    @NotBlank(message = "学院名称不能为空")
    private String collegeName;
    @NotBlank(message = "职位名称不能为空")
    private String position;
    @NotBlank(message = "性别不能为空")
    private String gender;
}

