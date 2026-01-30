package com.cjy.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjy.domain.Class;
import com.cjy.domain.College;
import com.cjy.domain.Major;
import com.cjy.domain.Result;
import com.cjy.mapper.ClassMapper;
import com.cjy.mapper.CollegeMapper;
import com.cjy.mapper.MajorMapper;
import com.cjy.service.IClassService;

@Service
public class ClassServiceImpl extends ServiceImpl<ClassMapper, Class> implements IClassService {

    @Autowired
    private CollegeMapper collegeMapper;
    @Autowired
    private MajorMapper majorMapper;
    
    
}
