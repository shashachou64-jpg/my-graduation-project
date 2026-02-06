package com.cjy.vo;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherInfoDTO {
    private Long id;
    private String name;
    private String collegeName;
    private String positionName;
    private String gender;
}
