package com.cjy.controller;

import java.util.List;

import com.cjy.domain.Position;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cjy.domain.Result;
import com.cjy.service.IPositionService;

@RestController
@RequestMapping("/position")
public class PositionController {
    @Autowired
    private IPositionService iPositionService;

    @GetMapping("/list")
    public Result list(){
        List<Position> positionList = iPositionService.list();
        return Result.success(positionList);
    }
}
