package com.example.aiinterview.service;

import com.example.aiinterview.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setNickname(rs.getString("nickname"));
            user.setAvatar(rs.getString("avatar"));
            user.setLevel(rs.getString("level"));
            user.setIsActive(rs.getBoolean("is_active"));
            user.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            user.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
            return user;
        }
    };

    /**
     * 查询所有用户
     */
    public List<User> findAll() {
        String sql = "SELECT id, username, email, nickname, avatar, level, is_active, created_at, updated_at FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    /**
     * 根据ID查询用户
     */
    public Optional<User> findById(Long id) {
        String sql = "SELECT id, username, email, nickname, avatar, level, is_active, created_at, updated_at FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * 根据用户名查询用户
     */
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, email, nickname, avatar, level, is_active, created_at, updated_at FROM users WHERE username = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, username);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * 创建新用户
     */
    public User save(User user) {
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        if (user.getLevel() == null) {
            user.setLevel("beginner");
        }
        
        String sql = "INSERT INTO users (username, email, nickname, avatar, level, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getNickname(), 
                           user.getAvatar(), user.getLevel(), user.getIsActive(), user.getCreatedAt(), user.getUpdatedAt());
        
        // 获取生成的ID
        String findSql = "SELECT id, username, email, nickname, avatar, level, is_active, created_at, updated_at FROM users WHERE username = ? ORDER BY created_at DESC LIMIT 1";
        return jdbcTemplate.queryForObject(findSql, userRowMapper, user.getUsername());
    }

    /**
     * 更新用户信息
     */
    public User update(Long id, User user) {
        user.setUpdatedAt(LocalDateTime.now());
        String sql = "UPDATE users SET username = ?, email = ?, nickname = ?, avatar = ?, level = ?, is_active = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getNickname(), 
                           user.getAvatar(), user.getLevel(), user.getIsActive(), user.getUpdatedAt(), id);
        
        return findById(id).orElse(null);
    }

    /**
     * 删除用户
     */
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }

    /**
     * 获取用户总数
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM users";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}