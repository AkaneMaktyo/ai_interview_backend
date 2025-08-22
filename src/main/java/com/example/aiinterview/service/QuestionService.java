package com.example.aiinterview.service;

import com.example.aiinterview.entity.Question;
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
 * 题目服务
 */
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Question> questionRowMapper = new RowMapper<Question>() {
        @Override
        public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
            Question question = new Question();
            question.setId(rs.getLong("id"));
            question.setTitle(rs.getString("title"));
            question.setContent(rs.getString("content"));
            question.setQuestionType(rs.getString("question_type"));
            question.setDifficulty(rs.getString("difficulty"));
            question.setPosition(rs.getString("position"));
            question.setTags(rs.getString("tags"));
            question.setExpectedAnswer(rs.getString("expected_answer"));
            question.setEvaluationCriteria(rs.getString("evaluation_criteria"));
            question.setHints(rs.getString("hints"));
            question.setAiGenerated(rs.getBoolean("ai_generated"));
            question.setAiPrompt(rs.getString("ai_prompt"));
            question.setCreatedBy(rs.getObject("created_by", Long.class));
            question.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            question.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
            question.setIsActive(rs.getBoolean("is_active"));
            return question;
        }
    };

    /**
     * 查询所有题目
     */
    public List<Question> findAll() {
        String sql = "SELECT id, title, content, question_type, difficulty, position, tags, " +
                    "expected_answer, evaluation_criteria, hints, ai_generated, ai_prompt, " +
                    "created_by, created_at, updated_at, is_active FROM questions WHERE is_active = true";
        return jdbcTemplate.query(sql, questionRowMapper);
    }

    /**
     * 根据ID查询题目
     */
    public Optional<Question> findById(Long id) {
        String sql = "SELECT id, title, content, question_type, difficulty, position, tags, " +
                    "expected_answer, evaluation_criteria, hints, ai_generated, ai_prompt, " +
                    "created_by, created_at, updated_at, is_active FROM questions WHERE id = ? AND is_active = true";
        try {
            Question question = jdbcTemplate.queryForObject(sql, questionRowMapper, id);
            return Optional.ofNullable(question);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * 根据条件查询题目
     */
    public List<Question> findByCondition(String questionType, String difficulty, String position) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT id, title, content, question_type, difficulty, position, tags, ")
                  .append("expected_answer, evaluation_criteria, hints, ai_generated, ai_prompt, ")
                  .append("created_by, created_at, updated_at, is_active FROM questions WHERE is_active = true");
        
        if (questionType != null && !questionType.isEmpty()) {
            sqlBuilder.append(" AND question_type = '").append(questionType).append("'");
        }
        if (difficulty != null && !difficulty.isEmpty()) {
            sqlBuilder.append(" AND difficulty = '").append(difficulty).append("'");
        }
        if (position != null && !position.isEmpty()) {
            sqlBuilder.append(" AND position = '").append(position).append("'");
        }
        
        return jdbcTemplate.query(sqlBuilder.toString(), questionRowMapper);
    }

    /**
     * 创建新题目
     */
    public Question save(Question question) {
        LocalDateTime now = LocalDateTime.now();
        question.setCreatedAt(now);
        question.setUpdatedAt(now);
        if (question.getIsActive() == null) {
            question.setIsActive(true);
        }
        if (question.getAiGenerated() == null) {
            question.setAiGenerated(true);
        }
        
        String sql = "INSERT INTO questions (title, content, question_type, difficulty, position, tags, " +
                    "expected_answer, evaluation_criteria, hints, ai_generated, ai_prompt, " +
                    "created_by, created_at, updated_at, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql, 
            question.getTitle(), question.getContent(), question.getQuestionType(),
            question.getDifficulty(), question.getPosition(), question.getTags(),
            question.getExpectedAnswer(), question.getEvaluationCriteria(), question.getHints(),
            question.getAiGenerated(), question.getAiPrompt(), question.getCreatedBy(),
            question.getCreatedAt(), question.getUpdatedAt(), question.getIsActive()
        );
        
        // 获取生成的ID
        String findSql = "SELECT id, title, content, question_type, difficulty, position, tags, " +
                        "expected_answer, evaluation_criteria, hints, ai_generated, ai_prompt, " +
                        "created_by, created_at, updated_at, is_active FROM questions WHERE title = ? ORDER BY created_at DESC LIMIT 1";
        return jdbcTemplate.queryForObject(findSql, questionRowMapper, question.getTitle());
    }

    /**
     * 更新题目信息
     */
    public Question update(Long id, Question question) {
        question.setUpdatedAt(LocalDateTime.now());
        String sql = "UPDATE questions SET title = ?, content = ?, question_type = ?, difficulty = ?, " +
                    "position = ?, tags = ?, expected_answer = ?, evaluation_criteria = ?, hints = ?, " +
                    "updated_at = ? WHERE id = ?";
        
        jdbcTemplate.update(sql,
            question.getTitle(), question.getContent(), question.getQuestionType(),
            question.getDifficulty(), question.getPosition(), question.getTags(),
            question.getExpectedAnswer(), question.getEvaluationCriteria(), question.getHints(),
            question.getUpdatedAt(), id
        );
        
        return findById(id).orElse(null);
    }

    /**
     * 删除题目（逻辑删除）
     */
    public boolean deleteById(Long id) {
        String sql = "UPDATE questions SET is_active = false, updated_at = ? WHERE id = ?";
        int rows = jdbcTemplate.update(sql, LocalDateTime.now(), id);
        return rows > 0;
    }

    /**
     * 获取题目总数
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM questions WHERE is_active = true";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}