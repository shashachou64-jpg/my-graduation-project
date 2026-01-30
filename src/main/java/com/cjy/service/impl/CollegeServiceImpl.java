package com.cjy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjy.domain.College;
import com.cjy.mapper.CollegeMapper;
import com.cjy.service.ICollegeService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, College> implements ICollegeService {

}
