package com.cjy.service.impl;

import com.cjy.domain.teacherPosition;
import com.cjy.mapper.PositionMapper;
import com.cjy.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositonServiceImpl implements PositionService {
    @Autowired
    private PositionMapper positionMapper;


    @Override
    public List<teacherPosition> findPositionList() {
        return positionMapper.findPositionList();
    }
}
