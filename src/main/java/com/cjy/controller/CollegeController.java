package com.cjy.controller;

import com.cjy.domain.College;
import com.cjy.domain.Result;
import com.cjy.service.ICollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/college")
public class CollegeController {
    @Autowired
    private ICollegeService iCollegeService;

    // 获取所有学院
    @GetMapping("/list")
    public Result list(){
        // 调用service查询学院列表
        List<College> collegeList = iCollegeService.list();
        return Result.success(collegeList);
    }
}
