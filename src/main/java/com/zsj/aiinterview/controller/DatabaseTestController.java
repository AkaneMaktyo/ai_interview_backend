package com.zsj.aiinterview.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据库测试控制器
 */
@RestController
@RequestMapping("/api/database")

@RequiredArgsConstructor
public class DatabaseTestController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/test-connection")
    public String testConnection() {
        try {
            // 测试数据库连接
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return "✅ 数据库连接成功！";
        } catch (Exception e) {
            return "❌ 数据库连接失败: " + e.getMessage();
        }
    }

    @GetMapping("/tables")
    public Map<String, Object> getTables() {
        try {
            // 查询所有表名
            List<String> tables = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = 'ai_interview'", 
                String.class
            );
            return Map.of(
                "success", true,
                "tableCount", tables.size(),
                "tables", tables
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }

    @GetMapping("/user-count")
    public Map<String, Object> getUserCount() {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
            return Map.of(
                "success", true,
                "userCount", count
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }
}