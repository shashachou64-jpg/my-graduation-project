package com.cjy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjy.domain.Position;
import com.cjy.domain.teacherPosition;
import com.cjy.mapper.PositionMapper;
import com.cjy.service.IPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositonServiceImpl extends ServiceImpl<PositionMapper, Position> implements IPositionService {
    
}
