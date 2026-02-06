package com.cjy.controller;

import com.cjy.common.RabbitMQConstants;
import com.cjy.domain.Homework;
import com.cjy.dto.HomeworkMessageDTO;
import com.cjy.dto.PublishHomeworkDTO;
import com.cjy.service.IHomeworkService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cjy.common.Result;
import java.util.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/homework")
public class HomeworkController {
    @Autowired
    private IHomeworkService homeworkService;

    @PostMapping("/publish")
    public Result publishHomework(@RequestBody @Valid PublishHomeworkDTO dto) {
        log.info("接收发布作业请求，标题：{}，课程ID：{}", dto.getTitle(), dto.getCourseId());

        

        return homeworkService.publishHomework(dto);
    }
    
}
