package com.cjy.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupTotalVO {
    /**
     * 课程id
     */
    private Long courseId;
    /**
     * 课程名称
     */
    private String courseName;
    /**
     * 小组总数
     */
    private Long groupCount;
    /**
     * 学生总数
     */
    private Long studentCount;
    /**
     * 最大小组数
     */
    private Long MaxGroupCount;
    /**
     * 最小小组数
     */
    private Long MinGroupCount;
    /**
     * 已分组学生数
     */
    private Long groupedStuCount;
    /**
     * 未分组学生数
     */
    private Long ungroupedStuCount;
    /**
     * 小组信息列表
     */
    private List<GroupInfoVO> groupInfoList;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GroupInfoVO {
        /**
         * 小组id
         */
        private Long id;
        /**
         * 小组名称
         */
        private String groupName;
        /**
         * 小组学生人数
         */
        private Long studentCount;
        /**
         * 小组最大人数
         */
        private Long maxMembers;
        /**
         * 小组当前人数
         */
        private Long currentMembers;
        /**
         * 小组创建时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date createTime;
        /**
         * 小组状态
         */
        private Integer status;  //0: 完整小组, 1: 未满小组, 2: 已满小组
    }
}
