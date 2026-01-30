package com.cjy.service;


import org.springframework.stereotype.Service;

import com.cjy.domain.teacherPosition;

import java.util.List;

@Service
public interface PositionService {
    /**
     * 查询所有位置
     * @return 位置列表
     */
    List<teacherPosition> findPositionList();


}
