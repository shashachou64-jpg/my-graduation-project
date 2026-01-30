package com.cjy.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjy.domain.UserWithIdentity;

import java.util.List;

@Mapper
public interface UserWithIdentityMapper extends BaseMapper<UserWithIdentity>{
    
    /**
     * 批量插入用户身份
     */
    @Insert("<script>" +
            "INSERT INTO user_with_identity (user_id, identity_id) VALUES " +
            "<foreach collection='list' item='u' separator=','>" +
            "(#{u.userId}, #{u.identityId})" +
            "</foreach>" +
            "</script>")
    void insertBatch(@Param("list") List<UserWithIdentity> list);

    /**
     * 通过用户名（学号）批量更新 user_id
     */
    @Insert("<script>" +
            "UPDATE user_with_identity uwi " +
            "INNER JOIN user u ON uwi.user_id = u.id " +
            "SET uwi.user_id = u.id " +
            "WHERE uwi.user_id IS NULL OR uwi.user_id = 0" +
            "</script>")
    void updateUserIdByUsername();
}
