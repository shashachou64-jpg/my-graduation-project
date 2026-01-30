package com.cjy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjy.domain.teacherPosition;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PositionMapper extends BaseMapper<teacherPosition> {
    /**
     * 查询位置列表
     * @return 位置列表
     */
    @Select("select * from teacher_position")
    List<teacherPosition> findPositionList();
}
