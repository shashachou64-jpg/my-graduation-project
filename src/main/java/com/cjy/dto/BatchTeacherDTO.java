package com.cjy.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.cjy.utils.TrimConverter;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchTeacherDTO {
    @ExcelProperty(value = {"姓名", "教师姓名"})
    @NotBlank(message = "姓名不能为空")
    private String name;

    @ExcelProperty(value = {"职位", "教师职位", "职位名称", "职务"})
    @NotBlank(message = "职位名称不能为空")
    private String positionName;

    @ExcelProperty(value = {"学院", "教师学院", "学院名称", "任职学院", "院系", "院系名称", "所属院系"})
    @NotBlank(message = "学院名称不能为空")
    private String collegeName;

    @ExcelProperty(value = {"性别", "教师性别", "Sex"})
    @NotBlank(message = "性别不能为空")
    private String gender;
}
