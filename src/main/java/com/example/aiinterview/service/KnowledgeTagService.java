package com.example.aiinterview.service;

import com.example.aiinterview.entity.KnowledgeTag;
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
 * 知识点标签服务
 */
@Service
@RequiredArgsConstructor
public class KnowledgeTagService {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<KnowledgeTag> tagRowMapper = new RowMapper<KnowledgeTag>() {
        @Override
        public KnowledgeTag mapRow(ResultSet rs, int rowNum) throws SQLException {
            KnowledgeTag tag = new KnowledgeTag();
            tag.setId(rs.getLong("id"));
            tag.setName(rs.getString("name"));
            tag.setDescription(rs.getString("description"));
            tag.setCategory(rs.getString("category"));
            tag.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            tag.setIsActive(rs.getBoolean("is_active"));
            return tag;
        }
    };

    /**
     * 查询所有标签
     */
    public List<KnowledgeTag> findAll() {
        String sql = "SELECT id, name, description, category, created_at, is_active FROM knowledge_tags WHERE is_active = true";
        return jdbcTemplate.query(sql, tagRowMapper);
    }

    /**
     * 根据ID查询标签
     */
    public Optional<KnowledgeTag> findById(Long id) {
        String sql = "SELECT id, name, description, category, created_at, is_active FROM knowledge_tags WHERE id = ? AND is_active = true";
        try {
            KnowledgeTag tag = jdbcTemplate.queryForObject(sql, tagRowMapper, id);
            return Optional.ofNullable(tag);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * 根据分类查询标签
     */
    public List<KnowledgeTag> findByCategory(String category) {
        String sql = "SELECT id, name, description, category, created_at, is_active FROM knowledge_tags WHERE category = ? AND is_active = true";
        return jdbcTemplate.query(sql, tagRowMapper, category);
    }

    /**
     * 根据名称查询标签
     */
    public Optional<KnowledgeTag> findByName(String name) {
        String sql = "SELECT id, name, description, category, created_at, is_active FROM knowledge_tags WHERE name = ? AND is_active = true";
        try {
            KnowledgeTag tag = jdbcTemplate.queryForObject(sql, tagRowMapper, name);
            return Optional.ofNullable(tag);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * 创建新标签
     */
    public KnowledgeTag save(KnowledgeTag tag) {
        LocalDateTime now = LocalDateTime.now();
        tag.setCreatedAt(now);
        if (tag.getIsActive() == null) {
            tag.setIsActive(true);
        }
        if (tag.getCategory() == null || tag.getCategory().isEmpty()) {
            tag.setCategory("technical");
        }
        
        String sql = "INSERT INTO knowledge_tags (name, description, category, created_at, is_active) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, tag.getName(), tag.getDescription(), tag.getCategory(), tag.getCreatedAt(), tag.getIsActive());
        
        // 获取生成的ID
        String findSql = "SELECT id, name, description, category, created_at, is_active FROM knowledge_tags WHERE name = ? ORDER BY created_at DESC LIMIT 1";
        return jdbcTemplate.queryForObject(findSql, tagRowMapper, tag.getName());
    }

    /**
     * 更新标签信息
     */
    public KnowledgeTag update(Long id, KnowledgeTag tag) {
        String sql = "UPDATE knowledge_tags SET name = ?, description = ?, category = ? WHERE id = ?";
        jdbcTemplate.update(sql, tag.getName(), tag.getDescription(), tag.getCategory(), id);
        return findById(id).orElse(null);
    }

    /**
     * 删除标签（逻辑删除）
     */
    public boolean deleteById(Long id) {
        String sql = "UPDATE knowledge_tags SET is_active = false WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }

    /**
     * 获取标签总数
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM knowledge_tags WHERE is_active = true";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    /**
     * 获取所有分类
     */
    public List<String> getAllCategories() {
        String sql = "SELECT DISTINCT category FROM knowledge_tags WHERE is_active = true ORDER BY category";
        return jdbcTemplate.queryForList(sql, String.class);
    }
}