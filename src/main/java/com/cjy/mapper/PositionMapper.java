package com.cjy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjy.domain.Position;
import com.cjy.domain.teacherPosition;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PositionMapper extends BaseMapper<Position> {

}
