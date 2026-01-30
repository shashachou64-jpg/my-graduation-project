package com.cjy.controller;

import com.cjy.domain.Result;
import com.cjy.domain.Teacher;
import com.cjy.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teacher")
@Validated
public class TeacherController {
    @Autowired
    private TeacherService teacherService;
    // 查询老师列表
    @GetMapping("/list")
    public Result teacherList() {
        // 调用service查询老师列表
        List<Teacher> teacherList=teacherService.findTeacherList();
        return Result.success(teacherList);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Teacher  teacher){
        return teacherService.addTeacher(teacher);
    }

    @PostMapping("/delete")
    public Result delete(@RequestBody Teacher  teacher){
        return teacherService.deleteTeacher(teacher);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Teacher  teacher){
        return teacherService.updateTeacher(teacher);
    }


}
