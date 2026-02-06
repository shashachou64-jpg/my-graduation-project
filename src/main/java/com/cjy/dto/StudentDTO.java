package com.cjy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学生信息DTO，用于前端展示
 * 包含学生基本信息以及关联的学院、专业名称
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
    @NotBlank(message = "学号不能为空")
    public String number;

    @NotBlank(message = "学生姓名不能为空")
    public String name;

    @NotNull(message = "学院ID不能为空")
    public Long collegeId;

    @NotNull(message = "专业ID不能为空")
    public Long majorId;

    @NotBlank(message = "性别不能为空")
    public String gender;

    @NotNull(message = "入学年份不能为空")
    public Integer year;

    @NotNull(message = "班级ID不能为空")
    public Long classId;
}

