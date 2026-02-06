package com.cjy.dto;

import java.util.Date;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishHomeworkDTO {
    @NotBlank(message = "作业标题不能为空")
    private String title;

    private String description;
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;

    private Long groupId;

    private Date startTime;
    @NotNull(message = "截止时间不能为空")
    private Date deadline;
    @NotNull(message = "满分分数不能为空")
    private Long totalScore;

    private Date createTime;


}
