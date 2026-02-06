package com.cjy.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cjy.common.Result;
import com.cjy.dto.PersonalDTO;
import com.cjy.service.IPersonalService;
import com.cjy.utils.JwtUtil;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@Validated
@RequestMapping("/personal")
public class PersonalController {
    
    @Autowired
    private IPersonalService iPersonalService;

    @PutMapping("/updateProfile/{token}")
    public Result updatePersonalInfo(@PathVariable("token") String token, @Valid @RequestBody PersonalDTO personalInfo) {
        
        
        Long id=JwtUtil.getId(token);

        //更新个人信息
        Result result=iPersonalService.updatePersonalInfo(id, personalInfo);
        return result;
    }

}
