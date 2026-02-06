package com.cjy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchStudentDTO {
    @NotBlank(message = "有数据班级名称为空")
    private String className;
    @NotBlank(message = "有数据学院名称不能为空")
    private String collegeName;
    @NotBlank(message = "有数据性别不能为空")
    private String gender;
    @NotBlank(message = "有数据专业名称不能为空")
    private String majorName;
    @NotBlank(message = "有数据姓名不能为空")
    private String name;
    @NotBlank(message = "有数据学号不能为空")
    private String number;
    @NotNull(message = "有数据入学年份不能为空")
    private Long year;
}
