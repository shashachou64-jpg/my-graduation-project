package com.cjy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjy.domain.Major;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MajorMapper extends BaseMapper<Major> {

    /**
     * 根据专业ID查询专业信息
     * @param collegeID 学院ID
     * @return 专业信息列表
     */
    @Select("select * from major where college_id = #{collegeID}")
     List<Major> getMajorByID(Integer collegeID);



}
