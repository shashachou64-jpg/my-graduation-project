package com.cjy.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjy.domain.Personal;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PersonalMapper extends BaseMapper<Personal> {
    /**
     * 批量插入个人信息
     */
    @Insert("<script>" +
            "INSERT INTO personal (user_id, name, sex) VALUES " +
            "<foreach collection='list' item='p' separator=','>" +
            "(#{p.userId}, #{p.name}, #{p.sex})" +
            "</foreach>" +
            "</script>")
    void insertBatch(@Param("list") List<Personal> list);

    /**
     * 通过用户名（学号）批量更新 user_id
     */
    @Insert("<script>" +
            "UPDATE personal p " +
            "INNER JOIN user u ON p.name = u.username " +
            "SET p.user_id = u.id " +
            "WHERE p.user_id IS NULL OR p.user_id = 0" +
            "</script>")
    void updateUserIdByUsername();
}
