package com.cjy.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("`group`")
public class Group {
    /**
     * 小组id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 小组名称
     */
    private String groupName;
    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 最大人数
     */
    private Long maxMembers;
    /**
     * 当前人数
     */
    private Long currentMembers;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 备注
     */
    private String remark;
}
