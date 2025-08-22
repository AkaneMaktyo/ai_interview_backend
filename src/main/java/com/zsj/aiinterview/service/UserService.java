package com.zsj.aiinterview.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsj.aiinterview.entity.User;
import com.zsj.aiinterview.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务 - 使用MyBatis Plus
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    /**
     * 查询所有用户
     */
    public List<User> findAll() {
        return this.list();
    }

    /**
     * 根据ID查询用户
     */
    public Optional<User> findById(Long id) {
        User user = this.getById(id);
        return Optional.ofNullable(user);
    }

    /**
     * 根据用户名查询用户
     */
    public Optional<User> findByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 根据用户名查询单个用户记录，用于登录验证
        queryWrapper.eq("username", username);
        User user = this.getOne(queryWrapper);
        return Optional.ofNullable(user);
    }

    /**
     * 创建新用户
     */
    public User createUser(User user) {
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        if (user.getLevel() == null) {
            user.setLevel("beginner");
        }
        
        // 插入新用户记录到数据库
        this.save(user);
        return user;
    }

    /**
     * 更新用户信息
     */
    public User updateUser(Long id, User user) {
        user.setId(id);
        user.setUpdatedAt(LocalDateTime.now());
        // 根据ID更新用户的所有可修改字段
        this.updateById(user);
        return this.getById(id);
    }

    /**
     * 删除用户
     */
    public boolean deleteById(Long id) {
        // 物理删除用户记录（直接从数据库中删除）
        return this.removeById(id);
    }

    /**
     * 获取用户总数
     */
    public long getUserCount() {
        // 统计users表中的记录总数
        return this.count();
    }
}