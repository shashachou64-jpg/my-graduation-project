package com.cjy.controller;

import com.cjy.domain.Result;
import com.cjy.domain.Teacher;
import com.cjy.domain.teacherPosition;
import com.cjy.service.PositionService;
import com.cjy.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/position")
public class PositionController {
    @Autowired
    private PositionService positionService;



    @GetMapping("/list")
    public Result list(){
        // 调用service查询位置列表
        List<teacherPosition> positionList=positionService.findPositionList();
        return Result.success(positionList);
    }


}
