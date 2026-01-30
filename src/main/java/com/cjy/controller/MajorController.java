package com.cjy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cjy.domain.Major;
import com.cjy.domain.Result;
import com.cjy.service.IMajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/major")
public class MajorController {
    @Autowired
    private IMajorService iMajorService;

    
    @GetMapping("/majorByCollegeID")
    public Result majorByCollegeID(@RequestParam Integer collegeId) {
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_id", collegeId);
        List<Major> majorList = iMajorService.list(queryWrapper);
        return Result.success(majorList);
    }




}
