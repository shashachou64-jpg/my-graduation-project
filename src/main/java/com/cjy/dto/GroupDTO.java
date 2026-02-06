package com.cjy.dto;

import java.sql.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDTO {
    private String groupName;
    private Long courseId;
    private Long maxMembers;
    private Long currentMembers;
    private Integer status;
    private Date createTime;
    private String remark;
    private List<StudentInfo> studentInfoList;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StudentInfo {
        private String number;
        private String name;
    }
}
