package com.cjy.controller;

import com.cjy.domain.Result;
import com.cjy.domain.Teacher;
import com.cjy.domain.dto.EditTeacherDTO;
import com.cjy.domain.dto.TeacherDTO;
import com.cjy.domain.vo.TeacherVO;
import com.cjy.service.ITeacherService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/teacher")
@Validated
public class TeacherController {
    @Autowired
    private ITeacherService iTeacherService;

    /**
     * 添加老师
     * 
     * @param teacher
     * @return
     */
    @PostMapping("/add")
    public Result add(@Valid @RequestBody TeacherDTO teacherDTO) {
        // 验证请求对象
        if (teacherDTO == null) {
            return Result.error("请求数据不能为空");
        }
        // 添加老师
        return iTeacherService.addTeacher(teacherDTO);
    }

    @GetMapping("/list")
    public Result list() {
        List<TeacherVO> teacherList = iTeacherService.getAllTeacherInfo();
        return Result.success(teacherList);
    }

    @DeleteMapping("/delete")
    public Result delete(@RequestParam String username) {
        if (username == null) {
            return Result.error("该教师不存在，无法删除");
        }
        return iTeacherService.deleteTeacher(username);
    }

    @PutMapping("/update")
    public Result update(@Valid @RequestBody EditTeacherDTO editTeacherDTO) {
        return iTeacherService.editTeacherInfo(editTeacherDTO);
    }

    @PostMapping("/batchAddTeaInfo")
    public Result batchAddTeaInfo(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }
        try {
            Result result = iTeacherService.batchAddTeaInfo(file);
            return result;
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
