package com.example.aiinterview.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aiinterview.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserRepository extends BaseMapper<User> {

    /**
     * 根据用户名查找用户
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    /**
     * 根据邮箱查找用户
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);

    /**
     * 根据用户名或邮箱查找用户
     */
    @Select("SELECT * FROM users WHERE username = #{usernameOrEmail} OR email = #{usernameOrEmail}")
    User findByUsernameOrEmail(String usernameOrEmail);

    /**
     * 查找活跃用户
     */
    @Select("SELECT * FROM users WHERE is_active = true ORDER BY created_at DESC")
    List<User> findActiveUsers();
}