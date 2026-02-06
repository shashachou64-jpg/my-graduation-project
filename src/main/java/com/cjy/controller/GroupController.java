package com.cjy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cjy.common.Result;
import com.cjy.dto.AddGroupDTO;
import com.cjy.dto.GroupDTO;
import com.cjy.domain.Group;
import com.cjy.service.IGroupService;
import com.cjy.utils.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private IGroupService iGroupService;

    /**
     * 根据课程ID获取小组列表
     */
    @GetMapping("/listByCourseId")
    public Result listByCourseId(@RequestParam Long courseId) {
        List<Group> groupList = iGroupService.getGroupsByCourseId(courseId);
        return Result.success(groupList);
    }

    
}
