package com.cjy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cjy.domain.Class;
import com.cjy.domain.Result;
import com.cjy.service.IClassService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/class")
public class ClassController {
    @Autowired
    private IClassService iClassService;
    
    /**
     * 根据年份和专业id获取班级列表
     * @param year
     * @param majorId
     * @return
     */
    @GetMapping("getClsByMajorId")
    public Result getClsByMajorId(@RequestParam Integer year, @RequestParam Long majorId) {
        //将年份的前两位数字去掉
        String name = year.toString().substring(2);
        LambdaQueryWrapper<Class> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Class::getMajorId, majorId).like(Class::getName,"%" + name + "%");
        List<Class> classList = iClassService.list(queryWrapper);
        return Result.success(classList);
    }


    
    
}
