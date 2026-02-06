package com.cjy.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentVO {
    private String number;
    private String name;
    private String collegeName;
    private String majorName;
    private String className;
    private String year;
    private String gender;
}
