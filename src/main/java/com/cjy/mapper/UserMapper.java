package com.cjy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjy.domain.User;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据用户名查询用户
     * @param userName 用户名
     * @return 用户
     */
    @Select("select * from user where username=#{userName}")
    User findByName(String userName);
    /**
     * 注册用户
     * @param userName 用户名
     * @param password 密码
     */
    @Insert("insert into user(username,password) values(#{userName},#{password})")
    void register(String userName, String password);

    @Select("select password from user where username= #{userName}")
    void findPassword(String userName);

    /**
     * 批量插入用户
     */
    @Insert("<script>" +
            "INSERT INTO user (username, password) VALUES " +
            "<foreach collection='list' item='user' separator=','>" +
            "(#{user.username}, #{user.password})" +
            "</foreach>" +
            "</script>")
    void insertBatch(@Param("list") List<User> list);
}
