package com.cjy.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditTeacherDTO {
    @NotBlank(message = "教师用户名不能为空")
    private String username;
    @NotBlank(message = "教师姓名不能为空")
    private String name;
    @NotNull(message = "学院ID不能为空")
    private Long collegeId;
    @NotNull(message = "职位ID不能为空")
    private Long positionId;
    @NotBlank(message = "性别不能为空")
    private String gender;
}
