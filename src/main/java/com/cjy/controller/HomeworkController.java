package com.cjy.controller;

import com.cjy.dto.PublishHomeworkDTO;
import com.cjy.service.IHomeworkService;
import com.cjy.utils.JwtUtil;

import cn.hutool.jwt.JWT;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cjy.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RestController
@RequestMapping("/homework")
public class HomeworkController {
    

    @Autowired
    private IHomeworkService homeworkService;

    /**
     * 发布作业
     * 
     * @param dto
     * @return
     */
    @PostMapping("/publish")
    public Result publishHomework(@RequestBody @Valid PublishHomeworkDTO dto) {
        log.info("接收发布作业请求，标题：{}，课程ID：{}", dto.getTitle(), dto.getCourseId());
        return homeworkService.publishHomework(dto);
    }

    /**
     * 查询教师发布的作业
     * 
     * @param teacherId
     * @return
     */
    @GetMapping("/list/{teacherId}")
    public Result listHomework(@PathVariable Long teacherId) {
        log.info("接收查询作业请求，教师ID：{}", teacherId);
        return homeworkService.listHomeworkByTeacherId(teacherId);
    }

    /**
     * 学生作业列表
     */
    @GetMapping("/student/list/{token}")
    public Result listHomeworkStu(@PathVariable String token) {
        String id = JwtUtil.getUsername(token);
        return homeworkService.listHomeworkByStudentId(id);
    }

    /**
     * 教师查看作业详情
     */
    @GetMapping("/teacher/homework/detail/{homeworkId}")
    public Result detailHomework(
            @PathVariable Long homeworkId,
            @RequestParam Long teacherId,
            @RequestParam(required = false) Integer pagenum,
            @RequestParam(defaultValue = "25") Integer pagesize) {
        if(pagenum != null){
            return homeworkService.detailHomeworkPage(homeworkId, teacherId, pagenum, pagesize);
        }
        //如果pagenum为空，则普通查询
        return homeworkService.detailHomeworkAll(homeworkId, teacherId);

    }

}
