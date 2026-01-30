package com.cjy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjy.domain.Major;
import com.cjy.mapper.MajorMapper;
import com.cjy.service.IMajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements IMajorService {
    
}
