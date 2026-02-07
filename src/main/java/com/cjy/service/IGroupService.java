package com.cjy.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cjy.common.Result;
import com.cjy.domain.Group;
import com.cjy.dto.AddGroupDTO;

public interface IGroupService extends IService<Group> {

    /**
     * 根据课程ID获取小组列表
     */
    List<Group> getGroupsByCourseId(Long courseId);

    
}
