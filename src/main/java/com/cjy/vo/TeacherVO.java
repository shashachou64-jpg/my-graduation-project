package com.cjy.vo;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherVO {
    private Long username;
    private String name;
    private String positionName;
    private String collegeName;
    private String gender;    
}
